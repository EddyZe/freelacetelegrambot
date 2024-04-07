package com.example.freelacetelegrambot.command;

import com.example.freelacetelegrambot.enums.Category;
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
                        
                        Email: %s.
                        Ваш номер телефона: %s.
                        Ваши избранные категории:
                        %s
                        """, user.getEmail(), user.getPhoneNumber(), userCategories);
    }

    public void addCategoryInSearchList(User user, Category category,
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
}
