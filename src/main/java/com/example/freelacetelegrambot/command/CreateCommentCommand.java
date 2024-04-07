package com.example.freelacetelegrambot.command;


import com.example.freelacetelegrambot.controller.CommentController;
import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.exception.OrderInvalidException;
import com.example.freelacetelegrambot.model.Comment;
import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.util.MessageHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CreateCommentCommand {

    private final OrderController orderController;
    private final CommentController commentController;

    public CreateCommentCommand(OrderController orderController, CommentController commentController) {
        this.orderController = orderController;
        this.commentController = commentController;
    }

    public Map<Long, String> execute(Message message, String text, Role role) {
        Map<Long, String> result = new HashMap<>();
        switch (role) {
            case CUSTOMER -> {
                sendCommentCustomer(message, text, result);
            }
            case EXECUTOR -> {
                sendCommentExecutor(message, text, result);
            }
        }

        return result;
    }

    private void sendCommentExecutor(Message message, String text, Map<Long, String> result) {
        String orderId = MessageHelper.getOrderIdFromMessage(message);
        Order order = orderController.findById(Long.parseLong(orderId));
        User recipient = order.getExecutor();
        User sender = order.getCustomer();
        String responseSender = String.format("Вы отправили отзыв: %s", recipient.getName());
        String responseRecipient = String.format("%s отправил вам отзыв", sender.getName());

        Comment comment = Comment.builder()
                .text(text)
                .recipient(recipient)
                .sender(sender)
                .createdAt(LocalDateTime.now())
                .build();
        commentController.save(comment);
        result.put(recipient.getChatId(), responseRecipient);
        result.put(sender.getChatId(), responseSender);
    }

    private void sendCommentCustomer(Message message, String text, Map<Long, String> response) {
        String orderId = MessageHelper.getOrderIdFromMessage(message);
        Order order = orderController.findById(Long.parseLong(orderId));
        if (order.getExecutor() == null) {
            throw new OrderInvalidException("Исполнитель еще не найден!");
        }
        String responseSender = String.format("Вы отправили отзыв: %s", order.getCustomer().getName());
        String responseRecipient = String.format("%s прислал вам отзыв", order.getExecutor().getName());
        Comment comment = Comment.builder()
                .text(text)
                .sender(order.getExecutor())
                .recipient(order.getCustomer())
                .createdAt(LocalDateTime.now())
                .build();
        response.put(order.getExecutor().getChatId(), responseSender);
        response.put(order.getCustomer().getChatId(), responseRecipient);
        commentController.save(comment);
    }

}
