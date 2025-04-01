package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Genre", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;
}