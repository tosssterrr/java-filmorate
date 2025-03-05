package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDateException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MemoryFilmService implements FilmService {

    private final FilmStorage storage;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate
            .of(1895, 12, 28);

    public Film createFilm(Film film) {
        validateReleaseDate(film.getReleaseDate());
        return storage.save(film);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new FilmDateException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return storage.findAll();
    }

    @Override
    public Film updateFilm(Film film) {
        if (storage.findById(film.getId()) == null) {
            throw new IdNotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        if (film.getReleaseDate() != null) {
            validateReleaseDate(film.getReleaseDate());
        }
        return storage.update(film);
    }
}
