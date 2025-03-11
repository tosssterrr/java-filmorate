package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Film createFilm(Film film);

    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    List<Film> getPopularFilms(int count);

    void addUserLike(long filmId, long userId);

    void deleteUserLike(long filmId, long userId);
}
