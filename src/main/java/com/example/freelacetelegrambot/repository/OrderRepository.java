package com.example.freelacetelegrambot.repository;

import com.example.freelacetelegrambot.enums.Category;
import com.example.freelacetelegrambot.model.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerChatId(Long chatId);
    List<Order> findByExecutorChatId(Long chatId);

    List<Order> findByCategory(Category category);

}
