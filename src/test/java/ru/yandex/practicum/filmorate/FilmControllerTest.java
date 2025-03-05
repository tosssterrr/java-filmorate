package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmDateException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.MemoryFilmService;
import ru.yandex.practicum.filmorate.storage.film.MemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

	private FilmController filmController;
	private MemoryFilmService filmService;

	@BeforeEach
	void setUp() {
		filmService = new MemoryFilmService(new MemoryFilmStorage());
		filmController = new FilmController(filmService);
	}

	@Test
	void updateFilm_ShouldUpdateAndReturnFilm() {
		Film film = new Film();
		film.setName("Initial Film");
		film.setDescription("Initial Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		Film createdFilm = filmService.createFilm(film);

		Film updatedFilm = new Film();
		updatedFilm.setId(createdFilm.getId());
		updatedFilm.setName("Updated Film");
		updatedFilm.setDescription("Updated Description");
		updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
		updatedFilm.setDuration(150);

		ResponseEntity<Film> response = filmController.updateFilm(updatedFilm);
		Film result = response.getBody();

		assertNotNull(result);
		assertEquals(updatedFilm.getName(), result.getName());
		assertEquals(updatedFilm.getDescription(), result.getDescription());
		assertEquals(updatedFilm.getReleaseDate(), result.getReleaseDate());
		assertEquals(updatedFilm.getDuration(), result.getDuration());
	}

	@Test
	void createFilm_ShouldCreateAndReturnFilm() {
		Film film = new Film();
		film.setName("New Film");
		film.setDescription("New Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		ResponseEntity<Film> response = filmController.createFilm(film);
		Film createdFilm = response.getBody();

		assertNotNull(createdFilm);
		assertEquals(film.getName(), createdFilm.getName());
		assertEquals(film.getDescription(), createdFilm.getDescription());
		assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
		assertEquals(film.getDuration(), createdFilm.getDuration());
	}

	@Test
	void getFilms_ShouldReturnListOfFilms() {
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Test Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		filmService.createFilm(film);

		ResponseEntity<Collection<Film>> response = filmController.getFilms();

		Collection<Film> films = response.getBody();

		assertNotNull(films);
		assertEquals(1, films.size());
		assertEquals(film.getName(), films.iterator().next().getName());
	}

	@Test
	void updateFilm_ShouldThrowException_WhenFilmDoesNotExist() {
		Film film = new Film();
		film.setId(567L);
		film.setName("Fail Film");
		film.setDescription("Fail Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		assertThrows(IdNotFoundException.class, () -> filmController.updateFilm(film));
	}

	@Test
	void createFilm_ShouldThrowException_WhenDatePast1895() {
		Film film = new Film();
		film.setName("New Film");
		film.setDescription("New Description");
		film.setReleaseDate(LocalDate.of(1895, 12, 27));
		film.setDuration(120);

		assertThrows(FilmDateException.class, () -> filmController.createFilm(film));
	}
}
