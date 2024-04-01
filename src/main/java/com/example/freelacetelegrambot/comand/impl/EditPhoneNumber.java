package com.example.freelacetelegrambot.comand.impl;


import com.example.freelacetelegrambot.comand.EditCommand;
import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.exception.UserNotValidException;
import com.example.freelacetelegrambot.model.User;
import org.springframework.stereotype.Component;

@Component
public class EditPhoneNumber implements EditCommand {

    private final UserController userController;

    public EditPhoneNumber(UserController userController) {
        this.userController = userController;
    }

    public String execute(String text, User user) {
        String phoneNumber = "^\\+\\d{11}";

        if(!text.matches(phoneNumber)) {
            throw new UserNotValidException("Не корректный номер. Введите номер в формате: +79998887766");
        }

        if (userController.findByPhoneNumber(text).isPresent())
            throw new UserNotValidException("Такой номер телефона уже занят");

        user.setPhoneNumber(text);
        userController.save(user);
        return "Вы изменили номер телефона на: " + text;
    }
}
