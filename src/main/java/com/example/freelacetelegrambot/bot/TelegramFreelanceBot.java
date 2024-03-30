package com.example.freelacetelegrambot.bot;

import com.example.freelacetelegrambot.comand.RegistrationCommand;
import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.Commands;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.enums.State;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.util.ReplyKeyboardInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class TelegramFreelanceBot extends TelegramLongPollingBot {
    private final String botUserName;

    private final Map<Long, String> userCommand = new HashMap<>();
    private final Map<Long, UserSingUpDTO> userRegistration = new HashMap<>();
    private final ReplyKeyboardInitializer keyboardInitializer;
    private final RegistrationCommand registrationCommand;
    private final UserController userController;


    public TelegramFreelanceBot(@Value("${telegram.bot.token}") String token,
                                @Value("${telegram.bot.username}") String botUserName,
                                ReplyKeyboardInitializer keyboardInitializer, RegistrationCommand registrationCommand, UserController userController) {
        super(token);
        this.botUserName = botUserName;
        this.keyboardInitializer = keyboardInitializer;
        this.registrationCommand = registrationCommand;
        this.userController = userController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            checkedCommands(message, chatId);
        }
    }

    private void checkedCommands(Message message, long chatId) {

        if (message.hasText()) {
            String text = message.getText();

            if (text.equals(Commands.START.toString())) {
                sendMessage(chatId, "Привет. Тут ты можешь выполнять" +
                                " и давать задания.\n" +
                                "Ты заказчик или исполнитель? Выбери и пройди" +
                                " регистрацию, если еще не зарегестрирован! (Пока что только для курьеров)",
                        keyboardInitializer.initKeyBoardStart());
            } else if (text.equals(Commands.CUSTOMER.toString()) || userCommand.containsKey(chatId)
                    && userCommand.get(chatId).equals(Commands.CUSTOMER.toString())) {
                Optional<User> user = userController.findByChatId(chatId);
                if (user.isEmpty())
                    registration(chatId, text, Role.CUSTOMER);
                else {
                    user.get().setRole(Role.CUSTOMER);
                    userController.save(user.get());
                }
            }
        }
    }

    private void sendMessage(long chatId, String message, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void registration(long chatId, String text, Role role) {
        UserSingUpDTO userSingUpDTO;
        if (userRegistration.containsKey(chatId)) {
            userSingUpDTO = userRegistration.get(chatId);
            String response = registrationCommand.execute(text, userSingUpDTO);
            sendMessage(chatId, response, keyboardInitializer.initKeyBoardStart());
            if (userSingUpDTO.getState() == State.BASIK) {
                userRegistration.remove(chatId);
                userCommand.remove(chatId);
            }
        }
        else {
            userSingUpDTO = UserSingUpDTO.builder()
                    .state(State.REGISTRATION_PHONE_NUMBER)
                    .role(role)
                    .build();
            userRegistration.put(chatId, userSingUpDTO);
            userCommand.put(chatId, Commands.CUSTOMER.toString());
            sendMessage(chatId, "Введите номер телефона: \n" +
                            "Если хотите отменить операцию введите 'Отмена'.",
                    keyboardInitializer.initKeyBoardStart());
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }
}
