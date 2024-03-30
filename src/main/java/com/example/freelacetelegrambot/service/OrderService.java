package com.example.freelacetelegrambot.service;


import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findByCustomerChatId(long chatId) {
        return orderRepository.findByCustomerChatId(chatId);
    }

    public List<Order> findByExecutorChatId(long chatId) {
        return orderRepository.findByExecutorChatId(chatId);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public void createTask(Order order) {
        orderRepository.save(order);
    }
}
