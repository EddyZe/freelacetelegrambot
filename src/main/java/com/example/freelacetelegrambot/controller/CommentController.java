package com.example.freelacetelegrambot.controller;


import com.example.freelacetelegrambot.model.Comment;
import com.example.freelacetelegrambot.service.CommentService;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    public List<Comment> findByRecipientChatId(long chatId) {
        return commentService.findByRecipientChatId(chatId);
    }

    public void save(Comment comment) {
        commentService.save(comment);
    }
}
