package com.example.freelacetelegrambot.comand;


import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.enums.Commands;
import com.example.freelacetelegrambot.enums.State;
import com.example.freelacetelegrambot.enums.StatusOrder;
import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CreateTaskCommand {

    private final OrderController orderController;

    public CreateTaskCommand(OrderController orderController) {
        this.orderController = orderController;
    }

    public String execute(String text, Order order, User user) {
        State state = user.getState();
        switch (state) {
            case CREATE_TASK_NAME -> {
                order.setName(text);
                user.setState(State.CREATE_TASK_DESCRIPTION);
                return "Введите описание задачи:";
            }
            case CREATE_TASK_DESCRIPTION -> {
                order.setDescription(text);
                user.setState(State.CREATE_TASK_PRICE);
                return "Введите цену:";
            }
            case CREATE_TASK_PRICE -> {
                order.setPrice(Double.parseDouble(text));
                user.setState(State.CRATE_TASK_CATEGORY);
                return "Выберете категорию: ";
            }
            case CREATE_TASK_ADDRESS -> {
                order.setOrderAddress(text);
                order.setCreatedAt(LocalDateTime.now());
                order.setCustomer(user);
                order.setStatus(StatusOrder.OPEN);
                user.setState(State.BASIK);
                orderController.createTask(order);
                return """
                        Вы добавили новое задание. Список ваших созданных заданий можно посмотреть нажав - 'Мои задания'.\s
                        Как только кто-то откликниться, вы получите увидомление. \s
                        После выбора исполнителя вам придут его контакты, а ему отправим ваши. \s
                        Так же будет доступен чат, для того, чтобы связаться внутри бота.
                        """;
            }
            default -> {
                return "Что-то пошло не так.";
            }
        }
    }
}
