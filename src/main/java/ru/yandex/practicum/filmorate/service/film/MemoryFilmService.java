package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDateException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Service
public class MemoryFilmService implements FilmService {

    private final HashMap<Long, Film> films = new HashMap<>();

    private static final LocalDate MIN_RELEASE_DATE = LocalDate
            .of(1895, 12, 28);
    private int counter = 0;

    public Film createFilm(Film film) {
        validateReleaseDate(film.getReleaseDate());
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new FilmDateException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new IdNotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        if (film.getReleaseDate() != null) {
            validateReleaseDate(film.getReleaseDate());
        }
        films.put(film.getId(), film);
        return film;
    }

    private long getNextId() {
        return ++counter;
    }
}
