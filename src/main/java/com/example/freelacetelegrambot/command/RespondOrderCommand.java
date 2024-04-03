package com.example.freelacetelegrambot.command;


import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.util.InlineKeyboardInitializer;
import com.example.freelacetelegrambot.util.ReplyKeyboardInitializer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.HashMap;
import java.util.Map;

@Component
public class RespondOrderCommand {

    private final OrderController orderController;
    private final InlineKeyboardInitializer inlineKeyboardInitializer;
    private final ReplyKeyboardInitializer replyKeyboardInitializer;

    public RespondOrderCommand(OrderController orderController, InlineKeyboardInitializer inlineKeyboardInitializer, ReplyKeyboardInitializer replyKeyboardInitializer) {
        this.orderController = orderController;
        this.inlineKeyboardInitializer = inlineKeyboardInitializer;
        this.replyKeyboardInitializer = replyKeyboardInitializer;
    }

    public Map<Long, Map<String, ReplyKeyboard>> execute (Message message, User user) {
        Map<Long, Map<String, ReplyKeyboard>> resultMap = new HashMap<>();
        long orderId = Long.parseLong(message.getText().split("\\.")[0].split(":")[1].trim());
        Order order = orderController.findById(orderId);
        long chatIdCustomer = order.getCustomer().getChatId();

//        if (user.getChatId() == chatIdCustomer)
//            throw new OrderInValidException("Вы не можете отправить отклик самому себе!");

        String responseExecutor = """ 
                        Вы откликнулись. Ждите ответа заказчика.
                        Если вас выберут, то мы пришлем данные заказчика. И вам будет доступен чат внутри бота.""";
        resultMap.put(user.getChatId(), Map.of(responseExecutor, replyKeyboardInitializer.initKeyBoardExecutor()));
        String responseCustomer = String.format("""
                Пришел отклик на ваш заказ.
                
                Номер задачи: %s
                Название задачи: %s.
                Описание задачи: %s.
                Желаемая цена: %s
                
                Откликнулся(-ась): %s
                Email: %s;""", order.getId(), order.getName(), order.getDescription(), order.getPrice(),
                user.getName(), user.getEmail());
        resultMap.put(chatIdCustomer, Map.of(responseCustomer, inlineKeyboardInitializer.initInlineKeyboardRespond()));
        return resultMap;
    }
}
