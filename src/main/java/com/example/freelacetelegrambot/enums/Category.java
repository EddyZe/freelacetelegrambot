package com.example.freelacetelegrambot.enums;

public enum Category {
    COURIER ("Курьер"),
    COURIER_WALKING("Пеший курьер " + new String(Character.toChars(0x1F6B6))),
    COURIER_AUTO("Курьер с авто " + new String(Character.toChars(0x1F697))),
    COURIER_BUY_AND_DELIVER("Купить и доставить " + new String(Character.toChars(0x1F6CD))),
    COURIER_URGENT_DELIVERY("Срочная дотавка " + new String(Character.toChars(0x1F680))),
    COURIER_FOOD_DELIVERY("Доставка еды " + new String(Character.toChars(0x1F35B))),
    COURIER_OTHER_DELIVERY("Разное " + new String(Character.toChars(0x1F4E6))),
    SEARCH_IN_CATEGORY("Искать"),
    COURIER_ALL_CATEGORY("Показать все подкатегории");

    private final String category;

    Category(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return category;
    }
}
