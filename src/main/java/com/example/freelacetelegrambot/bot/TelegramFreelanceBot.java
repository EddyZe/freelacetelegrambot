package com.example.freelacetelegrambot.bot;

import com.example.freelacetelegrambot.comand.CreateTaskCommand;
import com.example.freelacetelegrambot.comand.RegistrationCommand;
import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.Category;
import com.example.freelacetelegrambot.enums.Commands;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.enums.State;
import com.example.freelacetelegrambot.model.Order;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.util.InlineKeyboardInitializer;
import com.example.freelacetelegrambot.util.ReplyKeyboardInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
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
    private final Map<Long, Order> createdOrder = new HashMap<>();
    private final ReplyKeyboardInitializer keyboardInitializer;
    private final CreateTaskCommand createTaskCommand;
    private final InlineKeyboardInitializer inlineKeyboardInitializer;
    private final RegistrationCommand registrationCommand;
    private final UserController userController;


    public TelegramFreelanceBot(@Value("${telegram.bot.token}") String token,
                                @Value("${telegram.bot.username}") String botUserName,
                                ReplyKeyboardInitializer keyboardInitializer, CreateTaskCommand createTaskCommand,
                                InlineKeyboardInitializer inlineKeyboardInitializer,
                                RegistrationCommand registrationCommand, UserController userController) {
        super(token);
        this.botUserName = botUserName;
        this.keyboardInitializer = keyboardInitializer;
        this.createTaskCommand = createTaskCommand;
        this.inlineKeyboardInitializer = inlineKeyboardInitializer;
        this.registrationCommand = registrationCommand;
        this.userController = userController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            checkedCommands(message, chatId);
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            Message message = update.getCallbackQuery().getMessage();
            long chatId = message.getChatId();
            checkedCallBackQuery(message, chatId, callBackData);
        }
    }

    private void checkedCallBackQuery(Message message, long chatId, String callBackData) {
        if (callBackData.equals(Category.COURIER.name())) {

            String response = """
                    Выберите подкатегорию.
                    Подкатегорию указывать не обязатьльно. Если ее не указать,то задача будет отображаться во всем разделе.
                    Просто отправьте адрес, если желаете пропустить этот шаг.""";

            Order order = createdOrder.get(chatId);
            order.setCategory(Category.COURIER);
            User user = order.getCustomer();
            user.setState(State.CREATE_TASK_ADDRESS);
            editMessage(message, chatId, response,
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourier());

        }
    }

    private void checkedCommands(Message message, long chatId) {
        if (message.hasText()) {
            String text = message.getText();

            if (text.equals(Commands.START.toString())) {

                String response = String.format("Привет, %s. Я помогу тебе найти исполнителя или заказчика." +
                        "(Доступна только категория курьеры)", message.getChat().getFirstName());

                sendMessage(chatId, response, keyboardInitializer.initKeyBoardStart());

            } else if (text.equals(Commands.CUSTOMER.toString()) || userCommand.containsKey(chatId)
                            && userCommand.get(chatId).equals(Commands.CUSTOMER.toString())) {

                authorization(chatId, text, Role.CUSTOMER);

            } else if (text.equals(Commands.EXECUTOR.toString()) || userCommand.containsKey(chatId)
                             && userCommand.get(chatId).equals(Commands.EXECUTOR.toString())) {

                authorization(chatId, text, Role.EXECUTOR);

            } else if (text.equals(Commands.GO_BACK_ROLE.toString())) {

                sendMessage(chatId, "Хотите поменять роль?", keyboardInitializer.initKeyBoardStart());

            } else if (text.equals(Commands.CREATE_TASK.toString()) || userCommand.containsKey(chatId)
                            && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
                createOrder(chatId, text);
            }
        }
    }

    private void createOrder(long chatId, String text) {
        Optional<User> user = userController.findByChatId(chatId);
        if (user.isEmpty()) {
            String response = "Вы не зарегистрированы! Выберите рольк и пройдите регистрацию!";
            sendMessage(chatId, response, keyboardInitializer.initKeyBoardStart());
            return;
        }
        if (!checkAccountActive(chatId, user.get()))
            return;

        generateOrder(chatId, text, user.get());
    }

    private void generateOrder(long chatId, String text, User customer) {
        Order order;
        if (text.equalsIgnoreCase(Commands.CANCEL.toString())) {
            cancel(chatId);
        } else if (createdOrder.containsKey(chatId)) {
            order = createdOrder.get(chatId);
            customer = order.getCustomer();
            try {
                String response = createTaskCommand.execute(text, order, customer);
                ReplyKeyboard replyKeyboard = customer.getState() == State.CRATE_TASK_CATEGORY ?
                        inlineKeyboardInitializer.initInlineKeyboardSelectCategory() :
                        keyboardInitializer.initKeyBoardCustomer();
                sendMessage(chatId, response, replyKeyboard);
                if (customer.getState() == State.BASIK) {
                    createdOrder.remove(chatId);
                    userCommand.remove(chatId);
                }
            } catch (NumberFormatException e) {
                sendMessage(chatId, "Вводите только цифры!", keyboardInitializer.initKeyBoardCustomer());
            }
        } else {
            customer.setState(State.CREATE_TASK_NAME);
            order = Order.builder()
                    .customer(customer)
                    .build();
            createdOrder.put(chatId, order);
            userCommand.put(chatId, Commands.CREATE_TASK.toString());
            String response = "Если хотите отменить операцию введите 'Отмена'." +
                    "\nВведите название задачи:";
            sendMessage(chatId, response, keyboardInitializer.initKeyBoardCustomer());
        }
    }

    private void authorization(long chatId, String text, Role role) {
        Optional<User> user = userController.findByChatId(chatId);
        if (user.isPresent()) {
            user.get().setRole(role);
            userController.save(user.get());

            String response = role == Role.CUSTOMER ? "Вы вошли как заказчик" : "Вы вошли как исполнитель";

            ReplyKeyboardMarkup keyboardMarkup = role == Role.CUSTOMER ?
                    keyboardInitializer.initKeyBoardCustomer() : keyboardInitializer.initKeyBoardExecutor();

            sendMessage(chatId, response, keyboardMarkup);
        } else {
            registration(chatId, text, role);
        }
    }

    private boolean checkAccountActive(long chatId, User user) {
        if(user.getState() == State.NOT_ACTIVE_ACCOUNT) {
            String response = "Вы не подтвердили email. Перейдите по ссылке в письме.\n" +
                    "Так же вы можете отправить письмо повторно, или сменить адрес в настройках!";
            ReplyKeyboardMarkup replyKeyboardMarkup = user.getRole() == Role.CUSTOMER ?
                    keyboardInitializer.initKeyBoardCustomer() : keyboardInitializer.initKeyBoardExecutor();
            sendMessage(chatId, response, replyKeyboardMarkup);
            return false;
        }
        return true;
    }

    private void registration(long chatId, String text, Role role) {
        UserSingUpDTO userSingUpDTO;

        if (text.equalsIgnoreCase(Commands.CANCEL.toString())) {
            cancel(chatId);
        } else if (userRegistration.containsKey(chatId)) {
            ReplyKeyboardMarkup keyboardMarkup = keyboardInitializer.initKeyBoardStart();
            userSingUpDTO = userRegistration.get(chatId);
            String response = registrationCommand.execute(text, userSingUpDTO);
            if (userSingUpDTO.getState() == State.BASIK) {
                userRegistration.remove(chatId);
                userCommand.remove(chatId);
                keyboardMarkup = role == Role.CUSTOMER ?
                        keyboardInitializer.initKeyBoardCustomer() : keyboardInitializer.initKeyBoardExecutor();
            }
            sendMessage(chatId, response, keyboardMarkup);
        } else {
            userSingUpDTO = UserSingUpDTO.builder()
                    .chatId(chatId)
                    .state(State.REGISTRATION_PHONE_NUMBER)
                    .role(role)
                    .build();
            userRegistration.put(chatId, userSingUpDTO);
            userCommand.put(chatId, Commands.CUSTOMER.toString());

            sendMessage(chatId, "Если хотите отменить операцию введите 'Отмена'. \n" +
                            "Введите номер телефона:",
                    keyboardInitializer.initKeyBoardStart());
        }
    }

    private void sendMessage(long chatId, String message,
                             ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(replyKeyboard);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editMessage(Message message, long chatId, String text, InlineKeyboardMarkup replyKeyboard) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setText(text);
        if (replyKeyboard != null) {
            editMessageText.setReplyMarkup(replyKeyboard);
        }
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void cancel(long chatId) {
        userCommand.remove(chatId);

        if (userRegistration.containsKey(chatId)) {
            userCommand.remove(chatId);
            sendMessage(chatId, "Вы отменили регистрацию",
                    keyboardInitializer.initKeyBoardStart());
        }

        if (createdOrder.containsKey(chatId)) {
            createdOrder.get(chatId).getCustomer().setState(State.BASIK);
            createdOrder.remove(chatId);
            sendMessage(chatId, "Вы отменили создание задания.",
                    keyboardInitializer.initKeyBoardCustomer());
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }
}
