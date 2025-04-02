package ru.yandex.practicum.filmorate.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmRequestDto {
    @NotBlank(message = "Название фильма не может быть пустым")
    protected String name;

    @Size(max = 200, message = "Описание должно быть не длиннее 200 символов")
    protected String description;

    @NotNull(message = "Дата релиза обязательна")
    @ValidReleaseDate
    protected LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    protected Integer duration;

    @NotNull(message = "Рейтинг MPA обязателен")
    @Valid
    protected MpaIdDto mpa;

    @Valid
    protected Set<GenreIdDto> genres = new HashSet<>();
}

