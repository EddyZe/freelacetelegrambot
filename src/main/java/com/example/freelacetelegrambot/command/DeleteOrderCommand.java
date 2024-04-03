package com.example.freelacetelegrambot.command;

import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.model.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class DeleteOrderCommand {

    private final OrderController orderController;

    public DeleteOrderCommand(OrderController orderController) {
        this.orderController = orderController;
    }

    public String execute(Message message) {
        String orderId = message.getText().split("\n")[0]
                .split(":")[1]
                .replace(".", "");
        return orderController.deleteById(Long.parseLong(orderId.trim()));
    }
}
