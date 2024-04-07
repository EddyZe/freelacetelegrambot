package com.example.freelacetelegrambot.command;


import com.example.freelacetelegrambot.controller.UserController;
import com.example.freelacetelegrambot.enums.Category;
import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.model.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SearchExecutorCommand {

    private final UserController userController;

    public SearchExecutorCommand(UserController userController) {
        this.userController = userController;
    }

    public List<String> execute (Role role, List<Category> categories) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Set<User> users = findSelectedExecutor(role, categories);
        List<String> resultList = new ArrayList<>();
        if (users.isEmpty())
            return null;

        users.forEach(user -> {
            StringBuilder categoryUser = new StringBuilder();
            if (user.getLikeCategories().isEmpty())
                categoryUser.append("нет выбранных категорий");
            else {
                user.getLikeCategories().forEach(category ->
                    categoryUser.append("-").append(category.toString())
                            .append("\n"));
            }
            String response = String.format(
                    """
                            %s.\s
                            Категории:
                            %s
                            Email: %s.
                            Зарегестрирован: %s.
                            """,
                    user.getName(), categoryUser.substring(0, categoryUser.length()).trim(),
                    user.getEmail(), dtf.format(user.getCreatedAt()));
            resultList.add(response);
        });
        return resultList;
    }

    public Set<User> findSelectedExecutor(Role role, List<Category> categories) {
        List<User> executors = userController.findByRole(role);
        Set<User> resultList = new HashSet<>();
        for (User user : executors) {
            if (user.getLikeCategories() == null)
                user.setLikeCategories(new ArrayList<>());
            if (user.getLikeCategories().isEmpty()) {
                resultList.add(user);
                continue;
            }
            categories.forEach(category -> {
                if (user.getLikeCategories().contains(category))
                    resultList.add(user);
            });
        }
        return resultList;
    }
}
