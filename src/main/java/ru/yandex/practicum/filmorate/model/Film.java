package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

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

    private Set<Long> likes;

    public int getLikesCount() {
        return likes == null ? 0 : likes.size();
    }
}
