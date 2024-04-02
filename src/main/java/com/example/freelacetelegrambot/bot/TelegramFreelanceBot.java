package com.example.freelacetelegrambot.bot;

import com.example.freelacetelegrambot.command.*;
import com.example.freelacetelegrambot.command.EditEmailCommand;
import com.example.freelacetelegrambot.command.EditPhoneNumberCommand;
import com.example.freelacetelegrambot.controller.OrderController;
import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.*;
import com.example.freelacetelegrambot.exception.UserNotValidException;
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

import java.util.*;


@Component
public class TelegramFreelanceBot extends TelegramLongPollingBot {
    private final String botUserName;

    private final Map<Long, String> userCommand = new HashMap<>();
    private final Map<Long, UserSingUpDTO> userRegistration = new HashMap<>();
    private final Map<Long, Order> createdOrder = new HashMap<>();

    private final ReplyKeyboardInitializer keyboardInitializer;
    private final InlineKeyboardInitializer inlineKeyboardInitializer;

    private final OrderController orderController;
    private final UserController userController;

    private final RegistrationCommand registrationCommand;
    private final SearchTaskCommand searchTasksCommand;
    private final CreateTaskCommand createTaskCommand;
    private final ShowOrderOrTaskCommand showOrderOrTaskCommand;
    private final SearchExecutorCommand searchExecutorCommand;
    private final SettingCommand settingCommand;
    private final EditEmailCommand editEmailCommand;
    private final EditPhoneNumberCommand editPhoneNumber;


    public TelegramFreelanceBot(@Value("${telegram.bot.token}") String token,
                                @Value("${telegram.bot.username}") String botUserName,
                                ReplyKeyboardInitializer keyboardInitializer, CreateTaskCommand createTaskCommand,
                                OrderController orderController, InlineKeyboardInitializer inlineKeyboardInitializer,
                                RegistrationCommand registrationCommand, SearchTaskCommand searchTasksCommand,
                                ShowOrderOrTaskCommand showOrderOrTaskCommand, UserController userController,
                                SearchExecutorCommand searchExecutorCommand, SettingCommand settingCommand, EditEmailCommand editEmailCommand, EditPhoneNumberCommand editPhoneNumber) {
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
        this.searchExecutorCommand = searchExecutorCommand;
        this.settingCommand = settingCommand;
        this.editEmailCommand = editEmailCommand;
        this.editPhoneNumber = editPhoneNumber;
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
        String command;
        String selectedCategory = "Вы выбрали подкатегорию - '%s'." +
                " Введите адрес где нужно выполнить задачу.";
        StringBuilder selectCategory = new StringBuilder(
                "Чтобы убрать ненужные категории, нажмите на нее повторно. \n\nВыбранные категори: \n");

        if (!userCommand.containsKey(chatId)) {
            user = userController.findByChatId(chatId);
        }

        if (callBackData.equals(Category.COURIER.name())) {
            choosingCategories(message, chatId, user);

        } else if (callBackData.equals(Category.COURIER_AUTO.name())) {

            choosingCategory(message, chatId, user, selectCategory, String.format(selectedCategory,
                    Category.COURIER_AUTO), Category.COURIER_AUTO);

        } else if (callBackData.equals(Category.COURIER_BUY_AND_DELIVER.name())) {

            choosingCategory(message, chatId, user, selectCategory, String.format(selectedCategory,
                    Category.COURIER_BUY_AND_DELIVER), Category.COURIER_BUY_AND_DELIVER);

        } else if (callBackData.equals(Category.COURIER_FOOD_DELIVERY.name())) {

            choosingCategory(message, chatId, user, selectCategory, String.format(selectedCategory,
                    Category.COURIER_FOOD_DELIVERY), Category.COURIER_FOOD_DELIVERY);

        } else if (callBackData.equals(Category.COURIER_WALKING.name())) {

            choosingCategory(message, chatId, user, selectCategory, String.format(selectedCategory,
                    Category.COURIER_WALKING), Category.COURIER_WALKING);

        } else if (callBackData.equals(Category.COURIER_OTHER_DELIVERY.name())) {

            choosingCategory(message, chatId, user, selectCategory, String.format(selectedCategory,
                    Category.COURIER_OTHER_DELIVERY), Category.COURIER_OTHER_DELIVERY);

        } else if (callBackData.equals(Category.COURIER_URGENT_DELIVERY.name())) {
            choosingCategory(message, chatId, user, selectCategory, String.format(selectedCategory,
                    Category.COURIER_URGENT_DELIVERY), Category.COURIER_URGENT_DELIVERY);

        } else if (callBackData.equals(InlineKeyButton.DELETE.name())) {

            deleteOrder(message, chatId);

        } else if (callBackData.equals(Category.SEARCH_IN_CATEGORY.name())) {

            command = message.getText().split("\n\n")[0] + "\n\n";
            searchTasksOrExecutor(chatId, command, user);

        } else if (callBackData.equals(InlineKeyButton.ADD_LIKE_CATEGORY.name())) {

            assert user != null;
            String response = settingCommand.execute(user);
            editMessage(message, chatId, response,
                    inlineKeyboardInitializer.initInlineKeyboardAddLikeCategory());

        } else if (callBackData.equals(InlineKeyButton.GO_BACK_SETTING.name())) {

            assert user != null;
            String response = settingCommand.execute(user);
            editMessage(message, chatId, response, inlineKeyboardInitializer.initInlineKeyboardSetting());

        } else if (callBackData.equals(InlineKeyButton.EDIT_MAIL.name())) {
            assert user != null;
            ReplyKeyboardMarkup keyboardMarkup = user.getRole() == Role.CUSTOMER ?
                    keyboardInitializer.initKeyBoardCustomer() : keyboardInitializer.initKeyBoardExecutor();

            userCommand.put(chatId, InlineKeyButton.EDIT_MAIL.name());
            sendMessage(chatId, "Введите новый email:\nЧтобы отменить операцию, введите: 'отмена'", keyboardMarkup);
        } else if (callBackData.equals(InlineKeyButton.EDIT_PHONE_NUMBER.name())) {
            assert user != null;
            ReplyKeyboardMarkup keyboardMarkup = user.getRole() == Role.CUSTOMER ?
                    keyboardInitializer.initKeyBoardCustomer() : keyboardInitializer.initKeyBoardExecutor();

            userCommand.put(chatId, InlineKeyButton.EDIT_PHONE_NUMBER.name());
            sendMessage(chatId, "Введите новый номер:\nЧтобы отменить операцию, введите: 'отмена'", keyboardMarkup);

        } else if (callBackData.equals(InlineKeyButton.RESPOND.name())) {

            respondOnOrder(message, chatId, user);

        } else if (callBackData.equals(InlineKeyButton.GO_BACK_SELECT_CATEGORIES.name())) {
            assert user != null;
            command = message.getText().split("\n\n")[0] + "\n\n";
            if (command.startsWith("Настройки профиля")) {
                String response = settingCommand.execute(user);
                editMessage(message, chatId, response, inlineKeyboardInitializer.initInlineKeyboardAddLikeCategory());
            } else {
                editMessage(message, chatId, command + "Выберите категорию: ",
                        inlineKeyboardInitializer.initInlineKeyboardSelectCategory());
            }
        }

        else
            sendMessage(chatId, "Пока что я не умею это", null);
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
                sendMessage(chatId, "Поиск заданий.\n\nВыберите категорию:",
                        inlineKeyboardInitializer.initInlineKeyboardSelectCategory());

            } else if (text.equals(Commands.SEARCH_EXECUTOR.toString())) {
                sendMessage(chatId, "Поиск исполнителей.\n\nВыберите категорию:",
                        inlineKeyboardInitializer.initInlineKeyboardSelectCategory());

            } else if (text.equals(Commands.SETTING.toString())) {
                User user = userController.findByChatId(chatId);
                assert user != null;
                String response = settingCommand.execute(user);
                sendMessage(chatId, response,
                        inlineKeyboardInitializer.initInlineKeyboardSetting());

            } else if (userCommand.containsKey(chatId) &&
                    userCommand.get(chatId).equals(InlineKeyButton.EDIT_MAIL.name())) {

                editProfile(chatId, text, editEmailCommand);

            } else if (userCommand.containsKey(chatId) &&
                    userCommand.get(chatId).equals(InlineKeyButton.EDIT_PHONE_NUMBER.name())) {
                editProfile(chatId, text, editPhoneNumber);
            } else {
                sendMessage(chatId, "Пока что я не умею это", null);
            }
        }
    }

    private void editProfile(long chatId, String text, EditCommand command) {
        User user = userController.findByChatId(chatId);
        assert user != null;
        ReplyKeyboardMarkup keyboardMarkup = user.getRole() == Role.CUSTOMER ?
                keyboardInitializer.initKeyBoardCustomer() : keyboardInitializer.initKeyBoardExecutor();

        if (text.equalsIgnoreCase(Commands.CANCEL.toString())) {
            sendMessage(chatId, "Вы отменили операцию", keyboardMarkup);
            userCommand.remove(chatId);
            return;
        }

        try {
            String response = command.execute(text, user);
            sendMessage(chatId, response, keyboardMarkup);
            userCommand.remove(chatId);
        } catch (UserNotValidException e) {
            sendMessage(chatId, e.getMessage(), keyboardMarkup);
        }
    }

    private void deleteOrder(Message message, long chatId) {
        String response = "Задание удалено";
        String orderId = message.getText().split("\n")[0]
                .split(":")[1]
                .replace(".", "");
        orderController.deleteById(Long.parseLong(orderId.trim()));
        editMessage(message, chatId, response, null);
    }
    private void respondOnOrder(Message message, long chatId, User user) {
        assert user != null;
        if(!checkAccountActive(chatId, user))
            return;

        long orderId = Long.parseLong(message.getText().split("\\.")[0].split(":")[1].trim());
        Order order = orderController.findById(orderId);
        long chatIdCustomer = order.getCustomer().getChatId();

        if (chatId == chatIdCustomer) {
            sendMessage(chatId, "Вы не можете отправить отклик себе!",
                    keyboardInitializer.initKeyBoardExecutor());
            return;
        }

        String responseExecutor = """ 
                        Вы откликнулись. Ждите ответа заказчика.
                        Если вас выберут, то мы пришлем данные заказчика. И вам будет доступен чат внутри бота.""";
        sendMessage(chatId, responseExecutor,
                keyboardInitializer.initKeyBoardExecutor());
        String responseCustomer = String.format("""
                Пришел отклик на вашу задачу.
                -----------------------------
                Номер задачи: %s
                Название задачи: %s.
                Описание задачи: %s
                Желаемая цена: %s
                -----------------------------
                Откликнулся(-ась): %s
                Email: %s""", order.getId(), order.getName(), order.getDescription(), order.getPrice(),
                user.getName(), user.getEmail());
        sendMessage(chatIdCustomer, responseCustomer,
                inlineKeyboardInitializer.initInlineKeyboardRespond());
    }

    private void choosingCategory(Message message, long chatId, User user,
                                                StringBuilder selectCategory, String text, Category category) {
        String command;
        if (userCommand.containsKey(chatId) && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {

            selectCategory(chatId, category);
            editMessage(message, chatId, text, null);
            return;
        }

        assert user != null;
        command = message.getText().split("\n\n")[0] + "\n\n";

        if (command.startsWith("Настройки профиля")) {
            addCategoryInSearchList(user, category, selectCategory);
            String response = settingCommand.execute(user);
            editMessage(message, chatId, response,
                    inlineKeyboardInitializer.initInlineKeyboardAddLikeSubCategoriesCourier());
        } else {

            addCategoryInSearchList(user, category, selectCategory);
            editMessage(message, chatId, command + selectCategory,
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourier());
        }
        userController.save(user);
    }

    private void choosingCategories(Message message, long chatId, User user) {
        String command;
        if (userCommand.containsKey(chatId) && userCommand.get(chatId).equals(Commands.CREATE_TASK.toString())) {
            String response = "Выберите подкатегорию:";
            editMessage(message, chatId, response,
                    inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourierCreateOrder());
            return;
        }
        command = message.getText().split("\n\n")[0] + "\n\n";
        if (command.startsWith("Настройки профиля")) {
            String response = settingCommand.execute(user);
            editMessage(message, chatId, response,
                    inlineKeyboardInitializer.initInlineKeyboardAddLikeSubCategoriesCourier());
            return;
        }
        StringBuilder response = new StringBuilder("""
                Выберите подкатегории, в которых нужно искать. Так же будет показан результат из ваших избранных категорий. Выбранные категории будут автоматически добавлены в избранные.
                После того как сделаете выбор, нажмите кнопку икать.

                Выбранные категории:\s
                """);

        user.getLikeCategories().forEach(category ->
                response.append("-")
                        .append(category)
                        .append("\n"));

        editMessage(message, chatId, command + response,
                inlineKeyboardInitializer.inlineKeyboardMarkupSelectCategoryCourier());
    }

    private void searchTasksOrExecutor(long chatId, String command, User user) {
        if (command.startsWith("Поиск заданий.")) {
            searchTasks(chatId, user);
        } else {
            searchExecutors(chatId, user);
        }
    }

    private void searchExecutors(long chatId, User user) {
        List<String> resultList;
        resultList = searchExecutorCommand.execute(Role.EXECUTOR, user.getLikeCategories());
        if (resultList == null || resultList.isEmpty())
            sendMessage(chatId, "Пока что нет исполнителей в данных категориях.",
                    keyboardInitializer.initKeyBoardCustomer());
        else
            resultList.forEach(executor ->
                    sendMessage(chatId, executor, inlineKeyboardInitializer.initInlineKeyBoardSearchExecutor()));
    }

    private void searchTasks(long chatId, User user) {
        List<String> resultList;
        resultList = searchTasksCommand.execute(user.getLikeCategories());
        if (resultList == null || resultList.isEmpty())
            sendMessage(chatId, "Пока что нет заданий в данных категориях.",
                    keyboardInitializer.initKeyBoardExecutor());
        else {
            resultList.forEach(order ->
                    sendMessage(chatId, order, inlineKeyboardInitializer.initInlineKeyBoardSearchTask()));
        }
    }

    private void addCategoryInSearchList(User user, Category category,
                                         StringBuilder selectCategory) {
        if (user.getLikeCategories().contains(category))
            user.getLikeCategories().remove(category);
        else
            user.getLikeCategories().add(category);

        user.getLikeCategories().forEach(c ->
                selectCategory.append("-")
                        .append(c.toString())
                        .append(" \n"));
    }

    private void showMyOrdersOrTask(long chatId) {
        User user = userController.findByChatId(chatId);
        switch (user.getRole()) {
            case CUSTOMER -> {
                List<Order> orders = orderController.findByCustomerChatId(chatId);
                if (orders.isEmpty()) {
                    sendMessage(chatId, "Список ваших заданий пуст.",
                            keyboardInitializer.initKeyBoardCustomer());
                    return;
                }
                showOrderOrTaskCommand.showOrdersCustomer(orders).forEach(order ->
                        sendMessage(chatId, order, inlineKeyboardInitializer.initInlineKeyboardShowMyOrderCustomer()));
            }
            case EXECUTOR -> {
                List<Order> orders = orderController.findByExecutorChatId(chatId);
                if (orders.isEmpty()) {
                    sendMessage(chatId, "Список ваших задач пуст.",
                            keyboardInitializer.initKeyBoardExecutor());
                    return;
                }
                showOrderOrTaskCommand.showTasksExecutor(orders).forEach(order ->
                        sendMessage(chatId, order, inlineKeyboardInitializer.initInlineKeyboardShowMyOrderExecutor()));
            }
        }
    }

    private void createOrder(long chatId, String text) {
        User user = userController.findByChatId(chatId);
        generateOrder(chatId, text, user);
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
        Optional<User> user = userController.findByUserChatId(chatId);
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
        if (user.getState() == State.NOT_ACTIVE_ACCOUNT) {
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
            var name = chat.getUserName() != null ? chat.getUserName() :
                    chat.getFirstName();
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
            userRegistration.remove(chatId);
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
