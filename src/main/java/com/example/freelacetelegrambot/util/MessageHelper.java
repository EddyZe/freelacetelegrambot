package com.example.freelacetelegrambot.util;

import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageHelper {

    public static String getOrderIdFromMessage(Message message) {
        String[] strings = message.getText().split("\n");
        String orderId = null;
        for (String str : strings) {
            if (str.startsWith("Номер задания"))
                orderId = str.split(":")[1].trim().replaceAll("\\.", "");
        }
        return orderId;
    }

    public static String getEmailFromMessage(Message message) {
        String[] strings = message.getText().split("\n");
        String email = null;
        for(String str : strings) {
            if (str.startsWith("Email")) {
                email = str.split(":")[1].trim();
                email = email.substring(0, email.length()-1);
            }
        }
        return email;
    }
}
