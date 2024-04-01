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

    private String email;

    private String name;

    private String phoneNumber;

    private Role role;

    private State state;
}
