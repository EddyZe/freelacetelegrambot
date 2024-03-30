package com.example.freelacetelegrambot.util;


import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.Commands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Component
public class ReplyKeyboardInitializer {

    public ReplyKeyboardMarkup initKeyBoardStart() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add(Commands.CUSTOMER.toString());
        row.add(Commands.EXECUTOR.toString());
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(List.of(row));
        return replyKeyboardMarkup;
    }

}
