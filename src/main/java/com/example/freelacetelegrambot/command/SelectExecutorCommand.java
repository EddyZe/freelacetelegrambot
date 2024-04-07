package com.example.freelacetelegrambot.command;

import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.enums.StatusOrder;
import com.example.freelacetelegrambot.exception.OrderInvalidException;
import com.example.freelacetelegrambot.exception.UserNotFoundException;
import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SelectExecutorCommand {

    private final OrderController orderController;
    private final UserController userController;

    public SelectExecutorCommand(OrderController orderController, UserController userController) {
        this.orderController = orderController;
        this.userController = userController;
    }


    public Order execute(Message message) {
        String[] strings = message.getText().split("\n");
        Long orderId = null;
        String emailExecutor = null;
        for (String s : strings) {
            if (s.startsWith("Номер задания"))
                orderId = Long.parseLong(s.trim().split(":")[1].trim());
            if (s.startsWith("Email")) {
                emailExecutor = s.trim().split(":")[1].trim();
                emailExecutor = emailExecutor.substring(0, emailExecutor.length()-1);
            }
        }
        assert orderId != null;
        Order order = orderController.findById(orderId);
        User user = userController.findByEmail(emailExecutor)
                .orElseThrow(()-> new UserNotFoundException("Исполнитель не найден. Возможно он еще не зарегистрирован!"));

        if (order.getExecutor() != null)
            throw new OrderInvalidException("Вы уже выбрали исполнителя на эту задачу!");

        order.setExecutor(user);
        order.setStatus(StatusOrder.IN_EXECUTION);
        orderController.save(order);
        return order;
    }
}
