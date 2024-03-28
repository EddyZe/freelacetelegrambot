package com.example.freelacetelegrambot.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "comment")
public class Comment {


    @
    private long id;

    private String text;


}
