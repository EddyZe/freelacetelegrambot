package com.example.freelacetelegrambot.controller;

import com.example.freelacetelegrambot.enums.Category;
import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.service.OrderService;
import org.ietf.jgss.Oid;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<Order> findAll() {
        return orderService.findAll();
    }

    public List<Order> findByExecutorChatId(long chatId) {
        return orderService.findByExecutorChatId(chatId);
    }

    public List<Order> findByCustomerChatId(long chatId) {
        return orderService.findByCustomerChatId(chatId);
    }

    public void createTask(Order order) {
        orderService.createTask(order);
    }

    public Order findById(long id) {
        return orderService.findById(id);
    }

    public List<Order> findByCategory(Category category) {
        return orderService.findByCategory(category);
    }

    public void deleteById(long id) {
        orderService.deleteById(id);
    }
}
