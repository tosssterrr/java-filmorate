package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FilmRepositoryTest extends BaseTest {
    @Test
    public void testFindFilmById() {
        Film film = filmRepository.findById(1L);
        assertNotNull(film);
        assertEquals("Фильм 1", film.getName());
        assertEquals(2, film.getGenres().size()); // Если в data.sql есть жанры
    }

    @Test
    public void testCreateFilm() {
        Film newFilm = new Film();
        newFilm.setName("Новый фильм");
        newFilm.setDescription("Описание");
        newFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        newFilm.setDuration(120);
        newFilm.setMpa(mpaRepository.findById(1L));
        newFilm.setGenres(Arrays.asList(
                genreRepository.findById(3L),
                genreRepository.findById(5L)
        ));

        Film savedFilm = filmRepository.save(newFilm);
        assertNotNull(savedFilm);
        assertEquals(2, savedFilm.getGenres().size());
        assertEquals(3L, savedFilm.getGenres().get(0).getId()); // Проверка сортировки по ID
    }
}
