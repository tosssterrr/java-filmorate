package ru.yandex.practicum.filmorate.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class FilmUpdateDto extends FilmRequestDto {
    @NotNull(message = "Id не может быть пустым")
    private long id;
}
