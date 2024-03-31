package com.example.freelacetelegrambot.enums;

public enum StatusOrder {
    OPEN ("Открыт"),
    CLOSE("Закрыт"),
    IN_EXECUTION("В исполнении");

    private final String cmd;

    StatusOrder(String cmd) {
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
