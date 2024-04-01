package com.example.freelacetelegrambot.comand.impl;

import com.example.freelacetelegrambot.comand.EditCommand;
import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.exception.UserNotValidException;
import com.example.freelacetelegrambot.model.User;
import org.springframework.stereotype.Component;


@Component
public class EditEmailCommand implements EditCommand {

    private final UserController userController;

    public EditEmailCommand(UserController userController) {
        this.userController = userController;
    }

    public String execute(String text, User user) {
        String email = "^\\S+@\\S+\\.\\S+$";

        if(!text.matches(email)) {
            throw new UserNotValidException("Не корректный email. Попробуйте снова");
        }

        if (userController.findByEmail(text).isPresent())
            throw new UserNotValidException("Такой email уже занят");

        user.setEmail(text);
        userController.save(user);
        return "Вы изменили email на: " + text;
    }
}
