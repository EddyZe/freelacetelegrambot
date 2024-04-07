package com.example.freelacetelegrambot.service;

import com.example.freelacetelegrambot.model.Comment;
import com.example.freelacetelegrambot.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> findByRecipientChatId(long chatId) {
        return commentRepository.findByRecipientChatId(chatId);
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

}
