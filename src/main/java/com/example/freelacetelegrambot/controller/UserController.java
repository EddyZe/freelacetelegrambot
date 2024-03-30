package com.example.freelacetelegrambot.controller;

import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.exception.UserNotValidException;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import java.util.Optional;


@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void registration(UserSingUpDTO userSingUpDTO) {
        userService.registration(userSingUpDTO);
    }

    public void save(User user) {
        userService.save(user);
    }

    public Optional<User> findByChatId(long chatId) {
        return userService.findByChatId(chatId);
    }
}
