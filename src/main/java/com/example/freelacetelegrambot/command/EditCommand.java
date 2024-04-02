package com.example.freelacetelegrambot.command;

import com.example.freelacetelegrambot.model.User;

public interface EditCommand {
    String execute(String text, User user);

}
