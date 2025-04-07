package ru.yandex.practicum.filmorate.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MpaIdDto {
    @NotNull(message = "ID рейтинга MPA обязателен")
    private Long id;
}
