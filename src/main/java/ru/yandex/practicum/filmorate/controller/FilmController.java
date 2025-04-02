package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.controller.dto.FilmUpdateDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("User - {} likes Film - {}", userId, filmId);
        service.addUserLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("User - {} delete like from Film - {}", userId, filmId);
        service.deleteUserLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") int count) {
        if (count < 0) {
            count = 10;
        }
        Collection<Film> films = this.service.getPopularFilms(count);
        log.info("Get popular films - {}", films.size());
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable long id) {
        return ResponseEntity.ok(this.service.getFilmById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        Collection<Film> films = this.service.getAllFilms();
        log.info("Get all films - {}", films.size());
        return ResponseEntity.ok(films);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody FilmRequestDto filmDto) {
        log.info("trying creating film - {}", filmDto);
        Film createdFilm = this.service.createFilm(filmDto);
        log.info("Film created - {}", createdFilm);
        return ResponseEntity.ok(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody FilmUpdateDto filmDto) {
        log.info("trying update film - {}", filmDto);
        Film updatedFilm = this.service.updateFilm(filmDto);
        log.info("Film updated - {}", updatedFilm);
        return ResponseEntity.ok(updatedFilm);
    }

}
