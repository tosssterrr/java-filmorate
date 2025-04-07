package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.controller.dto.FilmUpdateDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film createFilm(FilmRequestDto filmDto);

    Collection<Film> getAllFilms();

    Film updateFilm(FilmUpdateDto filmDto);

    List<Film> getPopularFilms(int count);

    void addUserLike(long filmId, long userId);

    void deleteUserLike(long filmId, long userId);

    Film getFilmById(long id);
}
