package com.example.freelacetelegrambot.service;

import com.example.freelacetelegrambot.repository.CommentRepository;
import org.springframework.stereotype.Service;



@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

}
