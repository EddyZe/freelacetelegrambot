package com.example.freelacetelegrambot.enums;

public enum StatusOrder {
    OPEN ("Открыт" + new String(Character.toChars(0x231B))),
    CLOSE("Выполнен" + new String(Character.toChars(0x2705))),
    IN_EXECUTION("В исполнении"  + new String(Character.toChars(0x267B)));

    private final String cmd;

    StatusOrder(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }
}
