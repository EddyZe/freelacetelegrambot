package com.example.freelacetelegrambot.bot;

import com.example.freelacetelegrambot.comand.CreateTaskCommand;
import com.example.freelacetelegrambot.comand.RegistrationCommand;
import com.example.freelacetelegrambot.comand.SearchTaskCommand;
import com.example.freelacetelegrambot.comand.ShowOrderOrTaskCommand;
import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.*;
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

import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class TelegramFreelanceBot extends TelegramLongPollingBot {
    private final String botUserName;

    private final Map<Long, String> userCommand = new HashMap<>();
    private final Map<Long, UserSingUpDTO> userRegistration = new HashMap<>();
    private final Map<Long, Order> createdOrder = new HashMap<>();
    private final ReplyKeyboardInitializer keyboardInitializer;
    private final OrderController orderController;
    private final InlineKeyboardInitializer inlineKeyboardInitializer;
    private final RegistrationCommand registrationCommand;
    private final SearchTaskCommand searchTasksCommand;
    private final CreateTaskCommand createTaskCommand;
    private final ShowOrderOrTaskCommand showOrderOrTaskCommand;
    private final UserController userController;


    public TelegramFreelanceBot(@Value("${telegram.bot.token}") String token,
                                @Value("${telegram.bot.username}") String botUserName,
                                ReplyKeyboardInitializer keyboardInitializer, CreateTaskCommand createTaskCommand,
                                OrderController orderController, InlineKeyboardInitializer inlineKeyboardInitializer,
                                RegistrationCommand registrationCommand, SearchTaskCommand searchTasksCommand, ShowOrderOrTaskCommand showOrderOrTaskCommand,
                                UserController userController) {
        super(token);
        this.botUserName = botUserName;
        this.keyboardInitializer = keyboardInitializer;
        this.createTaskCommand = createTaskCommand;
        this.orderController = orderController;
        this.inlineKeyboardInitializer = inlineKeyboardInitializer;
        this.registrationCommand = registrationCommand;
        this.searchTasksCommand = searchTasksCommand;
        this.showOrderOrTaskCommand = showOrderOrTaskCommand;
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
        User user = null;
        StringBuilder selectCategory = new StringBuilder(
                "Чтобы убрать ненужные категории, нажмите на нее повторно. \n\nВыбранные категори: \n");
        if (!userCommand.containsKey(chatId)) {
            user = findUser(chatId);
        }
        if (callBackData.equals(Category.COURIER.name())) {
            if (userCommand.containsKey(chatId) && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
                String response = """
                        Выберите подкатегорию.
                        Подкатегорию указывать не обязатьльно. Если ее не указать,то задача будет отображаться во всем разделе.
                        Просто отправьте адрес, если желаете пропустить этот шаг.""";

                selectCategory(chatId, Category.COURIER);
                editMessage(message, chatId, response,
                        inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourierCreateOrder());
                return;
            }
            StringBuilder response = new StringBuilder("Выберите подкатегории, в которых нужно искать." +
                    " Так же будут показаны задания из ваших избранных категорий." +
                    " Выбранные категории будут автоматически добавлены в избранные.\n" +
                    "После того как сделаете выбор, нажмите кнопку икать.\n\n" +
                    "Выбранные категории: \n");

            user.getLikeCategories().forEach(category ->
                    response.append("-")
                            .append(category)
                            .append("\n"));

            sendMessage(chatId, response.toString(),
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourierSearchTasks());

        } else if (callBackData.equals(Category.COURIER_AUTO.name())) {

            if (userCommand.containsKey(chatId) && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
                String response = "Вы выбрали подкатегорию - 'курьер на авто'." +
                        " Введите адрес, где нужно выполнить задачу";

                selectCategory(chatId, Category.COURIER_AUTO);
                editMessage(message, chatId, response, null);
                return;
            }

            addCategoryInSearchList(user, Category.COURIER_AUTO, selectCategory);
            editMessage(message, chatId, selectCategory.toString(),
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourierSearchTasks());
            userController.save(user);

        } else if (callBackData.equals(Category.COURIER_BUY_AND_DELIVER.name())) {

            if (userCommand.containsKey(chatId) && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
                String response = "Вы выбрали подкатегорию - 'Купить и доставить'." +
                        " Введите адрес, где нужно выполнить задачу";

                selectCategory(chatId, Category.COURIER_BUY_AND_DELIVER);
                editMessage(message, chatId, response, null);
                return;
            }

            addCategoryInSearchList(user, Category.COURIER_BUY_AND_DELIVER, selectCategory);
            editMessage(message, chatId, selectCategory.toString(),
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourierSearchTasks());
            userController.save(user);

        } else if (callBackData.equals(Category.COURIER_FOOD_DELIVERY.name())) {

            if (userCommand.containsKey(chatId) && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
                String response = "Вы выбрали подкатегорию - 'Доставка еды'." +
                        " Введите адрес, где нужно выполнить задачу";

                selectCategory(chatId, Category.COURIER_FOOD_DELIVERY);
                editMessage(message, chatId, response, null);
                return;
            }

            addCategoryInSearchList(user, Category.COURIER_FOOD_DELIVERY, selectCategory);
            editMessage(message, chatId, selectCategory.toString(),
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourierSearchTasks());
            userController.save(user);

        } else if (callBackData.equals(Category.COURIER_WALKING.name())) {
            if (userCommand.containsKey(chatId) && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
                String response = "Вы выбрали подкатегорию - 'Пеший курьер'." +
                        " Введите адрес где нужно выполнить задачу.";

                selectCategory(chatId, Category.COURIER_WALKING);
                editMessage(message, chatId, response, null);
                return;
            }

            addCategoryInSearchList(user, Category.COURIER_WALKING, selectCategory);
            editMessage(message, chatId, selectCategory.toString(),
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourierSearchTasks());
            userController.save(user);

        } else if (callBackData.equals(Category.COURIER_OTHER_DELIVERY.name())) {
            if (userCommand.containsKey(chatId) && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
                String response = "Вы выбрали подкатегорию - 'Разное'." +
                        " Введите адрес где нужно выполнить задачу.";

                selectCategory(chatId, Category.COURIER_OTHER_DELIVERY);
                editMessage(message, chatId, response, null);
                return;
            }

            addCategoryInSearchList(user, Category.COURIER_OTHER_DELIVERY, selectCategory);
            editMessage(message, chatId, selectCategory.toString(),
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourierSearchTasks());
            userController.save(user);

        } else if (callBackData.equals(Category.COURIER_URGENT_DELIVERY.name())) {
            if (userCommand.containsKey(chatId) && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
                String response = "Вы выбрали подкатегорию - 'Срочная доставка'." +
                        " Введите адрес где нужно выполнить задачу.";

                selectCategory(chatId, Category.COURIER_URGENT_DELIVERY);
                editMessage(message, chatId, response, null);
                return;
            }

            addCategoryInSearchList(user, Category.COURIER_URGENT_DELIVERY, selectCategory);
            editMessage(message, chatId, selectCategory.toString(),
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourierSearchTasks());
            userController.save(user);

        } else if (callBackData.equals(InlineKeyButton.DELETE.name())) {
            String response = "Задание удалено";
            String orderId = message.getText().split("\n")[0]
                    .split(":")[1]
                    .replace(".", "");
            orderController.deleteById(Long.parseLong(orderId.trim()));
            editMessage(message, chatId, response, null);
        } else if (callBackData.equals(Category.SEARCH_IN_CATEGORY.name())) {
            List<String> resultList = searchTasksCommand.execute(user.getLikeCategories());
            if (resultList.isEmpty())
                sendMessage(chatId, "Пока что нет заданий в данных категориях.",
                        keyboardInitializer.initKeyBoardExecutor());
            else {
                resultList.forEach(order ->
                    sendMessage(chatId, order, inlineKeyboardInitializer.initInlineKeyBoardSearchTask()));
            }
        }
    }

    private void addCategoryInSearchList(User user, Category category, StringBuilder selectCategory) {
        if (user.getLikeCategories().contains(category))
            user.getLikeCategories().remove(category);
        else
            user.getLikeCategories().add(category);
        user.getLikeCategories().forEach(c ->
                selectCategory.append("-")
                .append(c.toString())
                .append(" \n"));
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

                authorization(message, text, Role.CUSTOMER);

            } else if (text.equals(Commands.EXECUTOR.toString()) || userCommand.containsKey(chatId)
                             && userCommand.get(chatId).equals(Commands.EXECUTOR.toString())) {

                authorization(message, text, Role.EXECUTOR);

            } else if (text.equals(Commands.GO_BACK_ROLE.toString())) {

                sendMessage(chatId, "Хотите поменять роль?", keyboardInitializer.initKeyBoardStart());

            } else if (text.equals(Commands.CREATE_TASK.toString()) || userCommand.containsKey(chatId)
                            && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
                createOrder(chatId, text);

            } else if (text.equals(Commands.MY_CREATED_TASKS.toString())) {
                showMyOrdersOrTask(chatId);
            } else if (text.equals(Commands.SEARCH_TASK.toString())) {
                sendMessage(chatId, "Выберите категорию", inlineKeyboardInitializer.initInlineKeyboardSelectCategory());
            }
        }
    }

    private void showMyOrdersOrTask(long chatId) {
        User user = findUser(chatId);
        if (user == null)
            return;
        switch (user.getRole()) {
            case CUSTOMER -> {
                List<Order> orders= orderController.findByCustomerChatId(chatId);
                if (orders.isEmpty()) {
                    sendMessage(chatId, "Список ваших заданий пуст.",
                            keyboardInitializer.initKeyBoardCustomer());
                    return;
                }
                showOrderOrTaskCommand.showOrdersCustomer(orders).forEach(order -> {
                    sendMessage(chatId, order, inlineKeyboardInitializer.initInlineKeyboardShowMyOrderCustomer());
                });
            } case EXECUTOR -> {
                List<Order> orders= orderController.findByExecutorChatId(chatId);
                if (orders.isEmpty()) {
                    sendMessage(chatId, "Список ваших задач пуст.",
                            keyboardInitializer.initKeyBoardExecutor());
                    return;
                }
                showOrderOrTaskCommand.showTasksExecutor(orders).forEach(order -> {
                    sendMessage(chatId, order, inlineKeyboardInitializer.initInlineKeyboardShowMyOrderExecutor());
                });
            }
        }
    }

    private void createOrder(long chatId, String text) {
        User user = findUser(chatId);

        if (user == null) return;

        generateOrder(chatId, text, user);
    }

    private User findUser(long chatId) {
        Optional<User> user = userController.findByChatId(chatId);
        if (user.isEmpty()) {
            String response = "Вы не зарегистрированы! Выберите роль и пройдите регистрацию!";
            sendMessage(chatId, response, keyboardInitializer.initKeyBoardStart());
            return null;
        }
        if (!checkAccountActive(chatId, user.get()))
            return null;
        return user.get();
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

    private void authorization(Message message, String text, Role role) {
        long chatId = message.getChatId();
        Optional<User> user = userController.findByChatId(chatId);
        if (user.isPresent()) {
            user.get().setRole(role);
            userController.save(user.get());

            String response = role == Role.CUSTOMER ? "Вы вошли как заказчик" : "Вы вошли как исполнитель";

            ReplyKeyboardMarkup keyboardMarkup = role == Role.CUSTOMER ?
                    keyboardInitializer.initKeyBoardCustomer() : keyboardInitializer.initKeyBoardExecutor();

            sendMessage(chatId, response, keyboardMarkup);
        } else {
            registration(message, text, role);
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

    private void registration(Message message, String text, Role role) {
        long chatId = message.getChatId();
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
            var chat = message.getChat();
            var name = chat.getUserName() == null ?
                    chat.getUserName() : chat.getFirstName() + " " + chat.getLastName();
            userSingUpDTO = UserSingUpDTO.builder()
                    .chatId(chatId)
                    .name(name)
                    .state(State.REGISTRATION_PHONE_NUMBER)
                    .role(role)
                    .build();
            userRegistration.put(chatId, userSingUpDTO);
            Commands commands = role == Role.EXECUTOR ? Commands.EXECUTOR : Commands.CUSTOMER;
            userCommand.put(chatId, commands.toString());

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

    private void selectCategory(long chatId, Category category) {
        Order order = createdOrder.get(chatId);
        order.setCategory(category);
        User user = order.getCustomer();
        user.setState(State.CREATE_TASK_ADDRESS);
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
