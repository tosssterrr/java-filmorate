package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Film.
 */
@Data
@Entity
@Table
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @Size(max = 201)
    private String description;

    @NotNull
    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive
    private int duration;

    @ManyToOne
    @JoinColumn(name = "mpa_id", referencedColumnName = "id")
    private Mpa mpa;

    @ManyToMany
    @JoinTable(
            name = "Film_genre",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    @Column(name = "likes_count")
    private Integer likesCount = 0;
}
