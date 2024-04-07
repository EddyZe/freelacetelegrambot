package com.example.freelacetelegrambot.command;


import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.util.MessageHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;

@Component
public class SendMessageExecutorOrCustomer {

    private final OrderController orderController;
    private final UserController userController;

    public SendMessageExecutorOrCustomer(OrderController orderController, UserController userController) {
        this.orderController = orderController;
        this.userController = userController;
    }



    public Map<Long, String> execute (Message message, Role role, String text) {
        Map<Long, String> responses = new HashMap<>();
        switch (role) {
            case CUSTOMER ->
                sendMessageCustomer(message, text, responses);
            case EXECUTOR ->
                sendMessageExecutor(message, text, responses);
        }
        return responses;
    }

    private void sendMessageExecutor(Message message, String text, Map<Long, String> responses) {
        Order order;
        String orderId = MessageHelper.getOrderIdFromMessage(message);
        order = orderController.findById(Long.parseLong(orderId));
        User sender = order.getExecutor();
        User recipient = order.getCustomer();
        generateResponse(text, responses, recipient, sender, order);
    }

    private void sendMessageCustomer(Message message, String text, Map<Long, String> responses) {
        Order order;
        String orderId = MessageHelper.getOrderIdFromMessage(message);
        order = orderController.findById(Long.parseLong(orderId));
        User sender = order.getCustomer();
        User recipient = order.getExecutor();
        generateResponse(text, responses, recipient, sender, order);
    }

    private void generateResponse(String text, Map<Long, String> responses, User recipient, User sender, Order order) {
        responses.put(recipient.getChatId(), String.format("""
                Вы отправили сообщение: %s.
                Название задания: %s.
                Номер задания: %s.
                Сообщение:
                %s""", sender.getName(), order.getName(), order.getId(), text));
        responses.put(sender.getChatId(),
                String.format("""
                        Вам сообщение от: %s.
                        Название заказа: %s.
                        Номер задания: %s.
                        Сообщение:
                        %s""", recipient.getName(), order.getName(), order.getId(), text));
    }
}
