package com.example.freelacetelegrambot.command;

import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.enums.Category;
import com.example.freelacetelegrambot.enums.StatusOrder;
import com.example.freelacetelegrambot.model.Order;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
public class SearchTaskCommand {

    private final OrderController orderController;

    public SearchTaskCommand(OrderController orderController) {
        this.orderController = orderController;
    }

    public List<String> execute(List<Category> categories) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        List<Order> orders = findSelectedOrder(categories);
        List<String> resultList = new ArrayList<>();
        if (orders.isEmpty())
            return null;

        orders.forEach(order -> {
            String response = String.format(
                    """
                            Номер задания: %s.
                            Заказчик: %s.
                            Название задания: %s.
                            Описание задания: %s.
                            Желаемая стоимость: %s
                            Категория: %s.
                            Статус: %s.
                            Адрес: %s.\s
                            Создано: %s.
                            """,
                    order.getId(), order.getCustomer().getName(), order.getName(), order.getDescription(), order.getPrice(),
                    order.getCategory().toString(),order.getStatus(), order.getOrderAddress(),
                    dtf.format(order.getCreatedAt()));
            if (order.getStatus() == StatusOrder.OPEN)
                resultList.add(response);
        });
        return resultList;
    }

    public List<Order> findSelectedOrder(List<Category> categories) {
        List<Order> allOrders = orderController.findAll();
        List<Order> resultList = new ArrayList<>();
        for (Order order : allOrders) {
            if (categories.contains(order.getCategory()))
                resultList.add(order);
            if (order.getCategory().name().equals(Category.COURIER.name()))
                resultList.add(order);
        }
        return resultList;
    }

}
