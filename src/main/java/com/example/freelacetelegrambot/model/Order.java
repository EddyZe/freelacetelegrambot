package com.example.freelacetelegrambot.model;


import com.example.freelacetelegrambot.enums.Category;
import com.example.freelacetelegrambot.enums.StatusOrder;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Order {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "order_address")
    private String orderAddress;

    @ManyToOne
    @JoinColumn(name = "customer", referencedColumnName = "id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "executor", referencedColumnName = "id")
    private User executor;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusOrder status;

    @Column(name = "price")
    private Double price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "category")
    private Category category;
}
