package com.manomay.autoposter.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordBotConfig {
    
    @Value("${discord.bot.token}")
    private String token;
    
    @Bean
    public JDA discordBot() throws InterruptedException {
        return JDABuilder.createDefault(token).build().awaitReady();
    }

}
