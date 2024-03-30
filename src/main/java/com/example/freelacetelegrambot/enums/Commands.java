package com.example.freelacetelegrambot.enums;

public enum Commands {
    START("/start"),
    EXECUTOR("Я исполнитель"),
    CUSTOMER("Я заказчик"),
    CANCEL("Отмена"),
    REGISTRATION("Регистрация");

    private final String cmd;

    Commands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }


    public boolean equals(String cmd) {
        return this.toString().equals(cmd);
    }
}
