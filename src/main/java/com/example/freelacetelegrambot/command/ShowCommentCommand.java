package com.example.freelacetelegrambot.command;


import com.example.freelacetelegrambot.controller.CommentController;
import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.exception.CommentNotFoundException;
import com.example.freelacetelegrambot.model.Comment;
import com.example.freelacetelegrambot.service.UserService;
import com.example.freelacetelegrambot.util.MessageHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShowCommentCommand {

    private final CommentController commentController;
    private final UserService userService;
    private final OrderController orderController;

    public ShowCommentCommand(CommentController commentController, UserService userService, OrderController orderController) {
        this.commentController = commentController;
        this.userService = userService;
        this.orderController = orderController;
    }

    public List<String> execute (Message message, Role role) {
        List<String> result = new ArrayList<>();
        Long chatId = null;

        switch (role) {
            case EXECUTOR -> {
                String email = MessageHelper.getEmailFromMessage(message);
                chatId = userService.findByEmail(email).orElseThrow().getChatId();
            }
            case CUSTOMER -> {
                String orderId = MessageHelper.getOrderIdFromMessage(message);
                chatId = orderController.findById(Long.parseLong(orderId)).getCustomer().getChatId();
            }
        }

        return getComments(result, chatId);
    }

    public List<String> execute(long chatId) {
        List<String> result = new ArrayList<>();
        return getComments(result, chatId);
    }

    private List<String> getComments(List<String> result, Long chatId) {
        List<Comment> comments;
        comments = commentController.findByRecipientChatId(chatId);

        if (comments == null || comments.isEmpty())
            return null;

        comments.forEach(comment -> {
            String com = String.format("""
                    От: %s
                    Кому: %s
                    Отзыв: %s""", comment.getSender().getName(), comment.getRecipient().getName(), comment.getText());
            result.add(com);
        });

        return result;
    }
}
