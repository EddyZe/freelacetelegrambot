package com.example.freelacetelegrambot.comand;

import com.example.freelacetelegrambot.model.User;

public interface EditCommand {
    String execute(String text, User user);

}
