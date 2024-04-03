package com.example.freelacetelegrambot.command;


import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.enums.State;
import com.example.freelacetelegrambot.exception.UserInValidException;
import org.springframework.stereotype.Controller;

@Controller
public class RegistrationCommand {

    private final UserController userController;

    public RegistrationCommand(UserController userController) {
        this.userController = userController;
    }


    public String execute(String text, UserSingUpDTO userSingUpDTO) {
        State state = userSingUpDTO.getState();
        String phoneNumber = "^\\+\\d{11}";
        String email = "^\\S+@\\S+\\.\\S+$";
        switch (state) {
            case REGISTRATION_PHONE_NUMBER -> {
                if (!text.matches(phoneNumber))
                    return "Введите номер телефона в формате: +79998887766";
                userSingUpDTO.setPhoneNumber(text);
                userSingUpDTO.setState(State.REGISTRATION_EMAIL);
                return "Введите email: ";
            }
            case REGISTRATION_EMAIL -> {
                if (!text.matches(email))
                    return "Не коректный email. Попробуйте снова.";
                userSingUpDTO.setEmail(text);
                try {
                    userController.registration(userSingUpDTO);
                    userSingUpDTO.setState(State.BASIK);
                    return userSingUpDTO.getRole() == Role.CUSTOMER ?
                            "Вы прошли регистрацию. Для того, чтобы пользоваться сервисом," +
                            " нужно подтвердить email. Мы отправили вам письмо" :

                            "Вы прошли регистрацию. Для того, чтобы пользоваться сервисом," +
                            " нужно подтвердить email. Мы отправили вам письмо. " +
                            "Настройте профиль в разделе 'Настройки профиля' и выбирите любимые категории," +
                            " чтобы заказчик смог вас найти";
                } catch (UserInValidException e) {
                    return e.getMessage();
                }
            }
            default -> {
                return "Что-то пошло не так!";
            }
        }
    }
}
