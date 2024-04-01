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

        var courierCategory = createButton("Услуги курьера", Category.COURIER);
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
        var messageCustomer = createButton("Отправить сообщение заказчику", InlineKeyButton.MESSAGE_CUSTOMER);
        var rejectionButton = createButton("Отказаться от задания", InlineKeyButton.REJECTION);
        var createCommentButton = createButton("Оставить отзыв заказчику", InlineKeyButton.CREATE_COMMENT);
        List<List<InlineKeyboardButton>> rowInLine =
                createListButton(messageCustomer, createCommentButton, rejectionButton);

        inlineKeyboardMarkup.setKeyboard(rowInLine);
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup initInlineKeyboardShowMyOrderCustomer() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var messageExecutor = createButton("Отправить сообщение исполнителю", InlineKeyButton.MESSAGE_EXECUTOR);
        var doneButton = createButton("Поставить статус: 'Выполнено'",
                InlineKeyButton.DONE);
        var refuseButton = createButton("Отказаться от исполнителя", InlineKeyButton.REFUSE);
        var createCommentButton = createButton("Оставить отзыв исполнителю", InlineKeyButton.CREATE_COMMENT);
        var deleteButton = createButton("Удалить", InlineKeyButton.DELETE);

        List<List<InlineKeyboardButton>> rowsLine =
                createListButton(messageExecutor, doneButton, createCommentButton, refuseButton, deleteButton);

        inlineKeyboardMarkup.setKeyboard(rowsLine);
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

    public InlineKeyboardMarkup inlineKeyboardMarkupSelectCategoryCourier() {
        inlineKeyboardMarkup = inlineKeyboardMarkupSelectCategoryCourierCreateOrder();

        var courierCategorySearch = createButton("Искать", Category.SEARCH_IN_CATEGORY);

        List<List<InlineKeyboardButton>> rowsInLine = inlineKeyboardMarkup.getKeyboard();
        rowsInLine.add(Collections.singletonList(courierCategorySearch));

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup initInlineKeyboardAddLikeCategory() {
        inlineKeyboardMarkup = initInlineKeyboardSelectCategory();
        var goBackSetting = createButton("Вернуться в настройки", InlineKeyButton.GO_BACK_SETTING);

        List<List<InlineKeyboardButton>> rowsInLine = inlineKeyboardMarkup.getKeyboard();
        rowsInLine.add(Collections.singletonList(goBackSetting));

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup initInlineKeyboardAddLikeSubCategoriesCourier() {
        inlineKeyboardMarkup = inlineKeyboardMarkupSelectCategoryCourierCreateOrder();

        var goBackSetting = createButton("Вернуться в настройки", InlineKeyButton.GO_BACK_SETTING);

        List<List<InlineKeyboardButton>> rowsInLine = inlineKeyboardMarkup.getKeyboard();
        rowsInLine.add(Collections.singletonList(goBackSetting));

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup initInlineKeyBoardSearchTask() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var respondButton = createButton("Откликнуться", InlineKeyButton.RESPOND);
        var commentButton = createButton("Отзывы заказчика", InlineKeyButton.SHOW_COMMENT);

        List<List<InlineKeyboardButton>> rowsInLine = createListButton(respondButton, commentButton);
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

    public InlineKeyboardMarkup initInlineKeyBoardSearchExecutor() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var respondButton = createButton("Связаться с исполнителем", InlineKeyButton.SEND_MESSAGE_EXECUTOR);
        var commentButton = createButton("Отзывы исполнителя", InlineKeyButton.SHOW_COMMENT);

        List<List<InlineKeyboardButton>> rowsInLine = createListButton(respondButton, commentButton);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup initInlineKeyboardSetting() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var editMail = createButton("Изменить email", InlineKeyButton.EDIT_MAIL);
        var resendTheMail = createButton("Отправить повторно письмо для активации", InlineKeyButton.RESEND_MAIL);
        var editPhoneNumber = createButton("Изменить номер телефона", InlineKeyButton.EDIT_PHONE_NUMBER);
        var addLikeCategories = createButton("Добавить или убрать любимые категории",
                InlineKeyButton.ADD_LIKE_CATEGORY);

        List<List<InlineKeyboardButton>> rowsLine = createListButton(editMail,
                editPhoneNumber, resendTheMail, addLikeCategories);
        inlineKeyboardMarkup.setKeyboard(rowsLine);
        return inlineKeyboardMarkup;
    }
}
