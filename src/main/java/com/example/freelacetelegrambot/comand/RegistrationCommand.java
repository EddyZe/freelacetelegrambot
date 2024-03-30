package com.example.freelacetelegrambot.comand;


import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.dto.UserSingUpDTO;
import com.example.freelacetelegrambot.enums.State;
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
                return "Введите email";
            }
            case REGISTRATION_EMAIL -> {
                if (!text.matches(email))
                    return "Не коректный email. Попробуйте снова.";
                userSingUpDTO.setEmail(text);
                userController.registration(userSingUpDTO);
                return "Вы прошли регистрацию. Для того, чтобы пользоваться сервисом," +
                        " нужно подтвердить email. Мы отправили вам письмо";
            }
            default -> {
                return "Что-то пошло не так!";
            }
        }
    }
}
