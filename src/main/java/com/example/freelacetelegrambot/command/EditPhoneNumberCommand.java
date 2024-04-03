package com.example.freelacetelegrambot.command;


import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.exception.UserInvalidException;
import com.example.freelacetelegrambot.model.User;
import org.springframework.stereotype.Component;

@Component
public class EditPhoneNumberCommand implements EditCommand {

    private final UserController userController;

    public EditPhoneNumberCommand(UserController userController) {
        this.userController = userController;
    }

    public String execute(String text, User user) {
        String phoneNumber = "^\\+\\d{11}";

        if(!text.matches(phoneNumber)) {
            throw new UserInvalidException("Не корректный номер. Введите номер в формате: +79998887766");
        }

        if (userController.findByPhoneNumber(text).isPresent())
            throw new UserInvalidException("Такой номер телефона уже занят");

        user.setPhoneNumber(text);
        userController.save(user);
        return "Вы изменили номер телефона на: " + text;
    }
}
