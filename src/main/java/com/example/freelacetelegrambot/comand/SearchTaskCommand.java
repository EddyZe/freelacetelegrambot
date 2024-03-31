package com.example.freelacetelegrambot.comand;

import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.enums.Category;
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

    public List<String> execute (List<Category> categories) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        List<Order> orders = findSelectedOrder(categories);
        List<String> resultList = new ArrayList<>();
        if (orders.isEmpty())
            return null;

        orders.forEach(order -> {
            String response = String.format(
                    """
                            Номер задания: %s.
                            Название задания: %s.
                            Описание задания: %s.
                            Желаемая стоимость: %s
                            Категория: %s.
                            Адрес: %s.\s
                            Создано: %s.
                            """,
                    order.getId(), order.getName(), order.getDescription(), order.getPrice(),
                    order.getCategory().toString(), order.getOrderAddress(),
                    dtf.format(order.getCreatedAt()));
            resultList.add(response);
        });
        return resultList;
    }

    public List<Order> findSelectedOrder(List<Category> categories) {
        List<Order> allOrders = orderController.findAll();
        List<Order> resultList = new ArrayList<>();
        if (categories.contains(Category.COURIER)) {
            for (Order order : allOrders) {
                if (order.getCategory().name().startsWith(Category.COURIER.name()))
                    resultList.add(order);
            }
        } else {
            for (Order order : allOrders) {
                if (categories.contains(order.getCategory()))
                    resultList.add(order);
            }
        }
        return resultList;
    }

}
