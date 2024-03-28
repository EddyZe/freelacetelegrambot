package com.example.freelacetelegrambot.config;


import com.example.freelacetelegrambot.bot.TelegramFreelanceBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class ApplicationConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramFreelanceBot telegramFreelanceBot)
            throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramFreelanceBot);
        return api;
    }
}
