package com.example.freelacetelegrambot.service;

import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.State;
import com.example.freelacetelegrambot.exception.UserNotFoundException;
import com.example.freelacetelegrambot.model.Comment;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.repository.CommentRepository;
import com.example.freelacetelegrambot.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public User findByChatId(long chatId) {
        return userRepository.findByChatId(chatId).orElseThrow(() ->
                new UserNotFoundException("Заказчик не найден или не зарегистрирован"));
    }

    public void registration (UserSingUpDTO userSingUpDTO) {
//        if (bindingResult.hasErrors()) {
//            StringBuilder errorsMessage = new StringBuilder();
//
//            bindingResult.getFieldErrors().forEach(fieldError -> errorsMessage
//                    .append(fieldError.getField())
//                    .append(" - ")
//                    .append(fieldError.getDefaultMessage())
//                    .append(";\n"));
//
//            throw new UserNotValidException(errorsMessage.toString());
//        }


        User user = convertToCustomer(userSingUpDTO);
        user.setState(State.BASIK);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }


    private User convertToCustomer(UserSingUpDTO userSingUpDTO) {
        return modelMapper.map(userSingUpDTO, User.class);
    }
}
