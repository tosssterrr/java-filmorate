package ru.yandex.practicum.filmorate.service.film.mpa;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
    Mpa createMpa(@Valid Mpa mpa);

    Mpa updateMpa(Mpa mpa);

    List<Mpa> getAllMpa();

    Mpa getMpa(long id);
}