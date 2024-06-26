package com.example.freelacetelegrambot.model;

import com.example.freelacetelegrambot.enums.Category;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.enums.State;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    @Column(name = "like_category")
    private List<Category> likeCategories = new ArrayList<>();

    @OneToMany(mappedBy = "recipient")
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
