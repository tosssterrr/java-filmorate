package ru.yandex.practicum.filmorate.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenreIdDto {
    @NotNull(message = "ID жанра обязателен")
    private Long id;
}
