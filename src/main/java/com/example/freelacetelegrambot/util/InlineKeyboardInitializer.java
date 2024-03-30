package com.example.freelacetelegrambot.util;

import com.example.freelacetelegrambot.enums.Category;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Component
public class InlineKeyboardInitializer {

    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public InlineKeyboardMarkup initInlineKeyboardSelectCategory() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var courierCategory = createButton("Услуги укурьера", Category.COURIER);
        List<List<InlineKeyboardButton>> rowsInLine = createListButton(courierCategory);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);

        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardButton createButton(String text, Category category) {
        var courierCategory = new InlineKeyboardButton();

        courierCategory.setText(text);
        courierCategory.setCallbackData(category.name());

        return courierCategory;
    }

    public InlineKeyboardMarkup inlineKeyboardMarkupSelectCategoryCourier() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var courierCategoryWalking = createButton("Пеший курьер", Category.COURIER_WALKING);
        var courierCategoryAuto = createButton("Курьер на авто", Category.COURIER_AUTO);
        var courierCategoryBuyAndDelivery = createButton("Купить и доставить", Category.COURIER_BUY_AND_DELIVER);
        var courierCategoryFoodDelivery = createButton("Доставка еды", Category.COURIER_FOOD_DELIVERY);
        var courierCategoryOtherDelivery = createButton("Разное", Category.COURIER_OTHER_DELIVERY);

        List<List<InlineKeyboardButton>> rowsInLine =
                createListButton(courierCategoryWalking, courierCategoryAuto, courierCategoryBuyAndDelivery,
                        courierCategoryFoodDelivery, courierCategoryOtherDelivery);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    private static List<List<InlineKeyboardButton>> createListButton(InlineKeyboardButton... inlineKeyboardButtons) {
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        Arrays.stream(inlineKeyboardButtons).toList().
                forEach(inlineKeyboardButton ->
                rowsInLine.add(Collections.singletonList(inlineKeyboardButton)));

        return rowsInLine;
    }
}
