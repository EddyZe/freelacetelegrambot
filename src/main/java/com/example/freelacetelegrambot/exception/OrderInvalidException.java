package com.example.freelacetelegrambot.exception;

public class OrderInvalidException extends RuntimeException{
    public OrderInvalidException(String msg) {
        super(msg);
    }
}
