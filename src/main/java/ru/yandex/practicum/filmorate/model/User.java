package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private long id;

    @Email
    @NotNull
    private String email;

    @NotBlank
    @NotNull
    private String login;

    private String name;

    @Past
    private LocalDate birthday;
}
