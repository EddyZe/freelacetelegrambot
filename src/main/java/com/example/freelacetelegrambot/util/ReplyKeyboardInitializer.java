package com.example.freelacetelegrambot.util;


import com.example.freelacetelegrambot.enums.Commands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Component
public class ReplyKeyboardInitializer {


    private ReplyKeyboardMarkup replyKeyboardMarkup;

    public ReplyKeyboardMarkup initKeyBoardStart() {
        replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add(Commands.CUSTOMER.toString());
        row.add(Commands.EXECUTOR.toString());
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(List.of(row));
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup initKeyBoardCustomer() {
        replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(Commands.CREATE_TASK.toString());
        row.add(Commands.SEARCH_EXECUTOR.toString());
        row.add(Commands.SETTING.toString());
        keyboardRows.add(row);
        return getDefaultReplyKeyboardMarkup(keyboardRows);
    }

    public ReplyKeyboardMarkup initKeyBoardExecutor() {
        replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(Commands.SEARCH_TASK.toString());
        row.add(Commands.SETTING.toString());
        keyboardRows.add(row);
        return getDefaultReplyKeyboardMarkup(keyboardRows);
    }

    private ReplyKeyboardMarkup getDefaultReplyKeyboardMarkup(List<KeyboardRow> keyboardRows) {
        KeyboardRow row = new KeyboardRow();
        row.add(Commands.MY_COMMENTS.toString());
        row.add(Commands.MY_CREATED_TASKS.toString());
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add(Commands.GO_BACK_ROLE.toString());
        keyboardRows.add(row);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
}
