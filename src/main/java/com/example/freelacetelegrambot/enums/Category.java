package com.example.freelacetelegrambot.enums;

public enum Category {
    COURIER ("Курьер"),
    COURIER_WALKING("Пеший курьер"),
    COURIER_AUTO("Курьер с авто"),
    COURIER_BUY_AND_DELIVER("Купить и доставить"),
    COURIER_URGENT_DELIVERY("Срочная дотавка"),
    COURIER_FOOD_DELIVERY("Доставка еды"),
    COURIER_OTHER_DELIVERY("Разное"),
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
