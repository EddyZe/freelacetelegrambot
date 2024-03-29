package com.example.freelacetelegrambot.service;

import com.example.freelacetelegrambot.model.Comment;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

}
