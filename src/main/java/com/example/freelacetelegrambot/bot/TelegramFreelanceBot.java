package com.example.freelacetelegrambot.bot;

import com.example.freelacetelegrambot.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class TelegramFreelanceBot extends TelegramLongPollingBot {

    private final String botUserName;
    private final UserService userService;


    public TelegramFreelanceBot(@Value("${telegram.bot.token}") String token,
                                @Value("${telegram.bot.username}") String botUserName, UserService userService) {
        super(token);
        this.botUserName = botUserName;
        this.userService = userService;
    }
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }
}
