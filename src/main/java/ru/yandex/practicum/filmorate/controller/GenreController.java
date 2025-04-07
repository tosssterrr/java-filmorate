package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.genre.GenreService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<?> getAllGenres() {
        List<Genre> genres = this.genreService.getAllGenres();
        log.info("Get all genres. Count - {}", genres.size());
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable long id) {
        Genre genre = this.genreService.getGenre(id);
        return ResponseEntity.ok(genre);
    }

    @PostMapping()
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) {
        Genre createdGenre = this.genreService.createGenre(genre);
        return ResponseEntity.ok(createdGenre);
    }

    @PutMapping()
    public ResponseEntity<Genre> updateGenre(@RequestBody Genre genre) {
        Genre updatedGenre = this.genreService.updateGenre(genre);
        return ResponseEntity.ok(updatedGenre);
    }
}
