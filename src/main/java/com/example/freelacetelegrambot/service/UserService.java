package com.example.freelacetelegrambot.service;

import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.enums.State;
import com.example.freelacetelegrambot.exception.UserInvalidException;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.repository.CommentRepository;
import com.example.freelacetelegrambot.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    public UserService(UserRepository userRepository, CommentRepository commentRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public Optional<User> findByChatId(long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public void registration (UserSingUpDTO userSingUpDTO) {
        if (userRepository.findByEmail(userSingUpDTO.getEmail()).isPresent())
            throw new UserInvalidException("Такой email занят! Повторите попытку");
        if (userRepository.findByPhoneNumber(userSingUpDTO.getPhoneNumber()).isPresent()) {
            userSingUpDTO.setState(State.REGISTRATION_PHONE_NUMBER);
            throw new UserInvalidException("Такой номер телефона занят! Повторите попытку");
        }
        User user = convertToCustomer(userSingUpDTO);
        user.setName(userSingUpDTO.getName());
        user.setState(State.BASIK); //TODO Изменить на NON_ACTIVE и настроить mailService
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }


    private User convertToCustomer(UserSingUpDTO userSingUpDTO) {
        return modelMapper.map(userSingUpDTO, User.class);
    }
}
