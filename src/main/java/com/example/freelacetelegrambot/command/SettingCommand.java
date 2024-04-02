package com.example.freelacetelegrambot.command;

import com.example.freelacetelegrambot.model.User;
import org.springframework.stereotype.Component;

@Component
public class SettingCommand {

    public String execute(User user) {
        StringBuilder userCategories = new StringBuilder();
        if (user.getLikeCategories() == null || user.getLikeCategories().isEmpty())
            userCategories.append("Вы еще не добавили не одной категории");
        else {
            user.getLikeCategories().forEach(category ->
                    userCategories.append("-").append(category.toString())
                            .append("\n"));
        }
        return String.format("""
                        Настройки профиля.
                        
                        Ваш email: %s.
                        Ваш номер телефона: %s.
                        Ваши избранные категории:
                        %s
                        """, user.getEmail(), user.getPhoneNumber(), userCategories);
    }
}
