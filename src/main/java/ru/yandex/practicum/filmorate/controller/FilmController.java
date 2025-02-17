package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.film.MemoryFilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    public FilmController() {
        this.service = new MemoryFilmService();
    }

    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        return ResponseEntity.ok(this.service.getAllFilms());
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        Film createdFilm = this.service.createFilm(film);
        return ResponseEntity.ok(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        Film updatedFilm = this.service.updateFilm(film);
        return ResponseEntity.ok(updatedFilm);
    }

}
