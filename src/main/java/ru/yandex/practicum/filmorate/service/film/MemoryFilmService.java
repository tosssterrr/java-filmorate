package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDateException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class MemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public MemoryFilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private static final LocalDate MIN_RELEASE_DATE = LocalDate
            .of(1895, 12, 28);

    public Film createFilm(Film film) {
        validateReleaseDate(film.getReleaseDate());
        return filmStorage.save(film);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new FilmDateException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmStorage.findById(film.getId()) == null) {
            throw new IdNotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        if (film.getReleaseDate() != null) {
            validateReleaseDate(film.getReleaseDate());
        }
        return filmStorage.update(film);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> allFilms = filmStorage.getAll();

        // сортировка по убыванию лайков и вторичный ключ — по id для стабильности
        allFilms.sort(Comparator
                .comparingInt(Film::getLikesCount).reversed()
                .thenComparing(Film::getId));

        return allFilms.subList(0, Math.min(count, allFilms.size()));
    }

    @Override
    public void addUserLike(long filmId, long userId) {
        userStorage.findById(userId);
        filmStorage.findById(filmId).getLikes().add(userId);
    }

    @Override
    public void deleteUserLike(long filmId, long userId) {
        userStorage.findById(userId);
        filmStorage.findById(filmId).getLikes().remove(userId);
    }
}
