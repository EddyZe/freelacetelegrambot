package com.example.freelacetelegrambot.repository;

import com.example.freelacetelegrambot.model.Comment;
import com.example.freelacetelegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySenderChatId(Long chatId);
    List<Comment> findByRecipientChatId(Long chatId);
}
