package com.manomay.autoposter.scheduler;

import com.manomay.autoposter.model.Message;
import com.manomay.autoposter.service.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.List;

@Component
public class PostScheduler {

    private final MessageService messageService;
    private final TelegramLongPollingBot telegramBot;

    @Value("${telegram.bot.channel}")
    private String channel;

    public PostScheduler(MessageService messageService, TelegramLongPollingBot telegramBot) {
        this.messageService = messageService;
        this.telegramBot = telegramBot;
    }

    @Scheduled(fixedRate = 60000)
    public void postMessages() {
        List<Message> messages = messageService.getPendingMessages();
        for (Message message : messages) {
            try {
                org.telegram.telegrambots.meta.api.methods.send.SendMessage post =
                    new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
                post.setChatId(channel);
                post.setText(message.getContent());
                telegramBot.execute(post);
                messageService.markAsPosted(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}