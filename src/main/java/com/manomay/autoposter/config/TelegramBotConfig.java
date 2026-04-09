package com.manomay.autoposter.config;

import com.manomay.autoposter.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotConfig.class);

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.owner-id}")
    private Long ownerId;

    private final MessageService messageService;

    public TelegramBotConfig(MessageService messageService) {
        this.messageService = messageService;
    }

    @Bean
    public TelegramLongPollingBot telegramBot() {
        return new TelegramLongPollingBot() {

            @Override
            public String getBotUsername() { return username; }

            @Override
            public String getBotToken() { return token; }

            @Override
            public void onUpdateReceived(Update update) {
                if (!update.hasMessage() || !update.getMessage().hasText()) return;
                log.info("Incoming message from ID: {}", update.getMessage().getFrom().getId());

                String text = update.getMessage().getText();
                Long senderId = update.getMessage().getFrom().getId();
                Long chatId = update.getMessage().getChatId();

                if (!ownerId.equals(senderId)) {
                    send(chatId, "Unauthorized.");
                    return;
                }

                if (text.equals("/start")) {
                    send(chatId, "Authenticated! Send me a message to queue it for posting.");
                } else {
                    messageService.saveMessage(senderId.toString(), text);
                    send(chatId, "Queued: " + text);
                }
            }

            private void send(Long chatId, String text) {
                SendMessage msg = new SendMessage();
                msg.setChatId(chatId.toString());
                msg.setText(text);
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    log.error("Failed to send message to chatId {}: {}", chatId, e.getMessage());
                }
            }
        };
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramLongPollingBot bot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        return api;
    }
}