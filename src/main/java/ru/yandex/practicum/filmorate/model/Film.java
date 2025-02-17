package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private long id;

    @NotBlank
    private String name;

    @Size(max = 201)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private int duration;
}
