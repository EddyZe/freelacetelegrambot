package com.example.freelacetelegrambot.repository;

import com.example.freelacetelegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(long chatId);
    Optional<User> findByEmail(String email);
}
