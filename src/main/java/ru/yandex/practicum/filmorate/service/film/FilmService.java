package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Film createFilm(Film film);

    Collection<Film> getAllFilms();

    Film updateFilm(Film film);
}
