package com.example.freelacetelegrambot.controller;

import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.exception.UserNotFoundException;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.service.UserService;
import org.springframework.stereotype.Controller;

import java.util.List;
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

    public User findByChatId(long chatId) {
        return userService.findByChatId(chatId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public Optional<User> findByUserChatId (long chatId) {
        return userService.findByChatId(chatId);
    }

    public Optional<User> findByEmail(String email) {
        return userService.findByEmail(email);
    }

    public List<User> findByRole(Role role) {
        return userService.findByRole(role);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userService.findByPhoneNumber(phoneNumber);
    }
}
