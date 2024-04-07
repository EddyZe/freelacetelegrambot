package com.example.freelacetelegrambot.command;


import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.enums.StatusOrder;
import com.example.freelacetelegrambot.exception.OrderInvalidException;
import com.example.freelacetelegrambot.model.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderDoneCommand {

    private final OrderController orderController;

    public OrderDoneCommand(OrderController orderController) {
        this.orderController = orderController;
    }

    public Map<Long, String> execute (long orderId) {
        Order order = orderController.findById(orderId);
        if (order.getStatus() == StatusOrder.OPEN)
            throw new OrderInvalidException("Вы не можете поставить заказу статус выполнен, т.к у него нет исполнителя");
        if (order.getStatus() == StatusOrder.CLOSE)
            throw new OrderInvalidException("Заказ уже выполнен!");

        Map<Long, String> response = new HashMap<>();

        long chatIdCustomer = order.getCustomer().getChatId();
        long chatIdExecutor = order.getExecutor().getChatId();

        order.setStatus(StatusOrder.CLOSE);
        orderController.save(order);

        String responseCustomer = "Заказ выполнен!" + new String(Character.toChars(0x2705)) +
                "\nТеперь вы можете оставить отзыв исполнителю! " +
                "Для этого перейдите в список задач и под нужной задачей нажмите: 'Оставить отзыв исполнителю'";
        String responseExecutor = "Вашу задачу отметили выполненой!\nНазвание задачи: " + order.getName() +
                "\nТеперь вы можете оставить отзыв заказчику." +
                " Для этого перейдите в список задач и под нужной задачей нажмите: 'Оставить отзыв заказчику'";

        response.put(chatIdCustomer, responseCustomer);
        response.put(chatIdExecutor, responseExecutor);

        return response;
    }
}
