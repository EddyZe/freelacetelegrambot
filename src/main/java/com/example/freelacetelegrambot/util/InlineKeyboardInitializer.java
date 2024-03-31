package com.example.freelacetelegrambot.util;

import com.example.freelacetelegrambot.enums.Category;
import com.example.freelacetelegrambot.enums.InlineKeyButton;
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

    private static InlineKeyboardButton createButton(String text, Enum<?> idButton) {
        var courierCategory = new InlineKeyboardButton();

        courierCategory.setText(text);
        courierCategory.setCallbackData(idButton.name());

        return courierCategory;
    }

    public InlineKeyboardMarkup initInlineKeyboardShowMyOrderExecutor() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var messageCustomer = createButton("Отправить сообщение заказчику",
                InlineKeyButton.MESSAGE_CUSTOMER);
        var doneButton = createButton("Поставить статус: 'Выполнено'",
                InlineKeyButton.DONE);
        var rejectionButton = createButton("Отказаться от задания",
                InlineKeyButton.REJECTION);
        List<List<InlineKeyboardButton>> rowInLine =
                createListButton(messageCustomer, doneButton, rejectionButton);

        inlineKeyboardMarkup.setKeyboard(rowInLine);
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup initInlineKeyboardShowMyOrderCustomer() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var messageExecutor = createButton("Отправить сообщение исполнителю", InlineKeyButton.MESSAGE_EXECUTOR);
        var editButton = createButton("Изменить", InlineKeyButton.EDIT);
        var closeButton = createButton("Закрыть", InlineKeyButton.CLOSE);
        var deleteButton = createButton("Удалить", InlineKeyButton.DELETE);

        List<InlineKeyboardButton> rowsLine = new ArrayList<>();
        rowsLine.add(editButton);
        rowsLine.add(closeButton);
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(Collections.singletonList(messageExecutor));
        rowsInLine.add(rowsLine);
        rowsInLine.add(Collections.singletonList(deleteButton));

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup inlineKeyboardMarkupSelectCategoryCourierCreateOrder() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var courierCategoryWalking = createButton("Пеший курьер", Category.COURIER_WALKING);
        var courierCategoryAuto = createButton("Курьер на авто", Category.COURIER_AUTO);
        var courierCategoryBuyAndDelivery = createButton("Купить и доставить", Category.COURIER_BUY_AND_DELIVER);
        var courierCategoryFoodDelivery = createButton("Доставка еды", Category.COURIER_FOOD_DELIVERY);
        var courierCategoryUrgentDelivery = createButton("Срочная доставка", Category.COURIER_URGENT_DELIVERY);
        var courierCategoryOtherDelivery = createButton("Разное", Category.COURIER_OTHER_DELIVERY);

        List<List<InlineKeyboardButton>> rowsInLine =
                createListButton(courierCategoryWalking, courierCategoryAuto, courierCategoryBuyAndDelivery,
                        courierCategoryFoodDelivery, courierCategoryUrgentDelivery, courierCategoryOtherDelivery);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup inlineKeyboardMarkupSelectCategoryCourierSearchTasks() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var courierCategoryWalking = createButton("Пеший курьер", Category.COURIER_WALKING);
        var courierCategoryAuto = createButton("Курьер на авто", Category.COURIER_AUTO);
        var courierCategoryBuyAndDelivery = createButton("Купить и доставить", Category.COURIER_BUY_AND_DELIVER);
        var courierCategoryFoodDelivery = createButton("Доставка еды", Category.COURIER_FOOD_DELIVERY);
        var courierCategoryUrgentDelivery = createButton("Срочная доставка", Category.COURIER_URGENT_DELIVERY);
        var courierCategoryOtherDelivery = createButton("Разное", Category.COURIER_OTHER_DELIVERY);
        var courierCategorySearch = createButton("Искать", Category.SEARCH_IN_CATEGORY);

        List<List<InlineKeyboardButton>> rowsInLine =
                createListButton(courierCategoryWalking, courierCategoryAuto, courierCategoryBuyAndDelivery,
                        courierCategoryFoodDelivery, courierCategoryUrgentDelivery, courierCategoryOtherDelivery,
                        courierCategorySearch);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup initInlineKeyBoardSearchTask() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var respondButton = createButton("Откликнуться", InlineKeyButton.RESPOND);

        List<List<InlineKeyboardButton>> rowsInLine = createListButton(respondButton);
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
