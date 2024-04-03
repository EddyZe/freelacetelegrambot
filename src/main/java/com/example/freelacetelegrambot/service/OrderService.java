package com.example.freelacetelegrambot.service;


import com.example.freelacetelegrambot.enums.Category;
import com.example.freelacetelegrambot.exception.OrderNotFoundException;
import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Order findById(long id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new OrderNotFoundException("Заказ с таким id не найден"));
    }

    public void createTask(Order order) {
        orderRepository.save(order);
    }

    public String deleteById(long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty())
            throw new OrderNotFoundException("Заказ с таким id не найден");
        if (order.get().getExecutor() != null)
            return "Вы не можете удалить задание. Т.к выбран исполнитель!";
        orderRepository.deleteById(id);
        return "Заказ удален";
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public List<Order> findByCategory(Category category) {
        return orderRepository.findByCategory(category);
    }
}
