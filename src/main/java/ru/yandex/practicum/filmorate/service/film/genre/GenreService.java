package ru.yandex.practicum.filmorate.service.film.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> getAllGenres();

    Genre getGenre(long id);

    Genre createGenre(Genre genre);

    Genre updateGenre(Genre genre);
}
