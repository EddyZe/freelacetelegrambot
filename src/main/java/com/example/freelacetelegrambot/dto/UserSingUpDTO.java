package com.example.freelacetelegrambot.dto;

import com.example.freelacetelegrambot.enums.Role;
import com.example.freelacetelegrambot.enums.State;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserSingUpDTO {

    private long chatId;

    @Email
    @NotEmpty(message = "Email не должен быть пустым.")
    private String email;

    private String name;

    @NotEmpty (message = "Номер телефона не должен быть пустым.")
    @Pattern(regexp = "^\\+\\d{11}", message = "Номер должен быть в формате: +79998887766")
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    private Role role;

    private State state;
}
