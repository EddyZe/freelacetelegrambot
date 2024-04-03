package com.example.freelacetelegrambot.enums;

public enum Commands {
    START("/start"),
    EXECUTOR("Я исполнитель"),
    CUSTOMER("Я заказчик"),
    CANCEL("Отмена"),
    GO_BACK_ROLE("Вернуться к выбору " + new String(Character.toChars(0x1F519))),
    CREATE_TASK("Создать задание " + new String(Character.toChars(0x2795))),
    MY_CREATED_TASKS("Мои задания " + new String((Character.toChars(0x1F4C3)))),

    MY_COMMENTS("Мои отзывы " + new String(Character.toChars(0x1F4AD))),
    SEARCH_EXECUTOR("Поиск исполнителя " + new String(Character.toChars(0x1F50D))),
    SEARCH_TASK("Поиск задач " + new String(Character.toChars(0x1F50D))),
    SETTING("Настройки профиля " + new String(Character.toChars(0x2699))),
    REGISTRATION("Регистрация");

    private final String cmd;

    Commands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

}
