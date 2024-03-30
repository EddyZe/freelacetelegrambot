package com.example.freelacetelegrambot.service;

import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.State;
import com.example.freelacetelegrambot.exception.UserNotValidException;
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


    public List<User> findAllCustomer() {
        return userRepository.findAll();
    }

    public Optional<User> findByChatId(long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public void registration (UserSingUpDTO userSingUpDTO) {
        if (userRepository.findByEmail(userSingUpDTO.getEmail()).isPresent())
            throw new UserNotValidException("Такой email занят! Повторите попытку");
        User user = convertToCustomer(userSingUpDTO);
        user.setState(State.BASIK); //TODO Изменить на NON_ACTIVE и настроить mailService
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }


    private User convertToCustomer(UserSingUpDTO userSingUpDTO) {
        return modelMapper.map(userSingUpDTO, User.class);
    }
}
