package ru.yandex.practicum.filmorate.storage.film;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
@NoArgsConstructor
public class MemoryFilmStorage implements FilmStorage {

    private final HashMap<Long, Film> films = new HashMap<>();
    private int counter = 0;

    @Override
    public Film save(Film film) {
        film.setLikes(new HashSet<>());
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findById(long id) {
        if (!films.containsKey(id)) {
            throw new IdNotFoundException("Фильм с id - " + id + "не найден");
        }
        return films.get(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    private long getNextId() {
        return ++counter;
    }
}
