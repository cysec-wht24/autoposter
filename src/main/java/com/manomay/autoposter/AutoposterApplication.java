package com.manomay.autoposter;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
public class AutoposterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutoposterApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner(TelegramLongPollingBot bot) {
        return args -> System.out.println("Bot username: " + bot.getBotUsername());
    }

}