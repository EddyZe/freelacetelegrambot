package com.example.freelacetelegrambot.enums;

public enum Commands {
    START("/start"),
    EXECUTOR("Я исполнитель"),
    CUSTOMER("Я заказчик"),
    CANCEL("Отмена"),
    GO_BACK_ROLE("Вернуться к выбору"),
    CREATE_TASK("Создать задание"),
    MY_CREATED_TASKS("Мои задания"),

    MY_COMMENTS("Мои отзывы"),
    SEARCH_EXECUTOR("Поиск исполнителя"),
    SEARCH_TASK("Поиск задач"),
    SETTING("Настройки профиля"),
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
