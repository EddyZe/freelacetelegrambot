package com.example.freelacetelegrambot.command;

import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.enums.StatusOrder;
import com.example.freelacetelegrambot.exception.OrderInvalidException;
import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class RejectionCommand {

    private final OrderController orderController;

    public RejectionCommand(OrderController orderController) {
        this.orderController = orderController;
    }

    public Map<Long, String> execute (long orderId, User user) {
        Order order = orderController.findById(orderId);
        Role role = user.getRole();
        Map<Long, String> response = new HashMap<>();

        switch (role) {
            case EXECUTOR -> {
                if (order.getStatus() == StatusOrder.CLOSE)
                    throw new OrderInvalidException("Вы не можете отказаться, от выполненого задания!");
                response.put(user.getChatId(), "Вы отказались от задания!" + new String(Character.toChars(0x274C)));
                response.put(order.getCustomer().getChatId(), user.getName()
                        + " отказался от вашего задания.\nЗадание: " + order.getName());
                updateOrder(order);
            }
            case CUSTOMER -> {
                if (order.getExecutor() == null)
                    throw new OrderInvalidException("Вы не можете отказаться от исполнителя, т.к он еще не выбран!");
                if (order.getStatus() == StatusOrder.CLOSE)
                    throw new OrderInvalidException("Вы не можете отказаться от исполнителя, т.к заказ уже выполнен");

                response.put(user.getChatId(), "Вы отказались от исполнителя!");
                response.put(order.getExecutor().getChatId(), user.getName()
                        + " отказался, чтобы вы выполняли задание.\nЗадание: " + order.getName());
                updateOrder(order);
            }
        }
        return response;
    }

    private void updateOrder(Order order) {
        order.setStatus(StatusOrder.OPEN);
        order.setExecutor(null);
        orderController.save(order);
    }
}
