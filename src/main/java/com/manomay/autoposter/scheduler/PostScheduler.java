package com.manomay.autoposter.scheduler;

import com.manomay.autoposter.model.Message;
import com.manomay.autoposter.service.LlmService;
import com.manomay.autoposter.service.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import net.dv8tion.jda.api.JDA;

import java.util.List;

@Component
public class PostScheduler {

    private final MessageService messageService;
    private final TelegramLongPollingBot telegramBot;
    private final LlmService llmService;
    private final JDA jda;

    @Value("${telegram.bot.channel}")
    private String channel;

    @Value("${discord.bot.channel-id}")
    private String discordChannelId;

    public PostScheduler(MessageService messageService, TelegramLongPollingBot telegramBot, LlmService llmService, JDA jda) {
        this.messageService = messageService;
        this.telegramBot = telegramBot;
        this.llmService = llmService;
        this.jda = jda;
    }

    @Scheduled(fixedRate = 60000)
    @SuppressWarnings("null")
    public void postMessages() {
        List<Message> messages = messageService.getPendingMessages();
        for (Message message : messages) {
            try {

                // Telegram
                org.telegram.telegrambots.meta.api.methods.send.SendMessage post =
                    new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
                post.setChatId(channel);
                String telegramText = llmService.generatePost(message.getContent(), message.getStreamLink(), "TELEGRAM");
                post.setText(telegramText);
                telegramBot.execute(post);

                // Discord
                String discordText = llmService.generatePost(message.getContent(), message.getStreamLink(), "DISCORD");
                var discordChannel = jda.getTextChannelById(discordChannelId);
                if (discordChannel != null && discordText != null) {
                    discordChannel.sendMessage(discordText).queue();
                } else {
                    System.err.println("Discord channel not found or text is null");
                }

                messageService.markAsPosted(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}