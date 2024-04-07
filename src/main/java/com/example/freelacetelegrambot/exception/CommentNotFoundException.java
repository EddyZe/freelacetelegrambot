package com.example.freelacetelegrambot.exception;


public class CommentNotFoundException extends RuntimeException{

    public CommentNotFoundException(String msg) {
        super(msg);
    }
}
