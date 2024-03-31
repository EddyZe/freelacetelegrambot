package com.example.freelacetelegrambot.comand;

import com.example.freelacetelegrambot.model.Order;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
public class ShowOrderOrTaskCommand {


    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public List<String> showTasksExecutor(List<Order> orders) {
        List<String> result = new ArrayList<>();
            orders.forEach(order -> {
                String response = String.format(
                        """
                                Номер задания: %s.
                                Название задания: %s.
                                Описание задания: %s.
                                Желаемая стоимость: %s
                                Категория: %s.
                                Заказчик: %s.
                                Адрес: %s.\s
                                Создано: %s.
                                """,
                        order.getId(), order.getName(), order.getDescription(), order.getPrice(),
                        order.getCategory().toString(), order.getCustomer().getName(),
                        order.getOrderAddress(),
                        dtf.format(order.getCreatedAt()));
                result.add(response);
            });
        return result;
    }

    public List<String> showOrdersCustomer(List<Order> orders) {
        List<String> result = new ArrayList<>();
        orders.forEach(order -> {
            String executor = order.getExecutor() == null ? "Не найден" :
                    order.getExecutor().getName();

            String response = String.format(
                    """
                            Номер задания: %s.
                            Название задания: %s.
                            Описание задания: %s.
                            Желаемая стоимость: %s
                            Категория: %s.
                            Исполнитель: %s.
                            Адрес: %s.\s
                            Создано: %s.
                            """,
                    order.getId(), order.getName(), order.getDescription(), order.getPrice(),
                    order.getCategory().toString(), executor, order.getOrderAddress(),
                    dtf.format(order.getCreatedAt()));
            result.add(response);
        });
        return result;
    }
}
