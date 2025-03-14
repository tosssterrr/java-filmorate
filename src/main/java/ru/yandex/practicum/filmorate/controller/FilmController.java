package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        Collection<Film> films = this.service.getAllFilms();
        log.info("Get all films - {}", films.size());
        return ResponseEntity.ok(films);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        Film createdFilm = this.service.createFilm(film);
        log.info("Film created - {}", film);
        return ResponseEntity.ok(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        Film updatedFilm = this.service.updateFilm(film);
        log.info("Film updated - {}", film);
        return ResponseEntity.ok(updatedFilm);
    }

}
