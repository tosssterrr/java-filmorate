package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.mpa.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @PostMapping
    public ResponseEntity<Mpa> createMpa(@RequestBody Mpa mpa) {
        Mpa createdMpa = this.mpaService.createMpa(mpa);
        log.info("Mpa created - {}", createdMpa.getName());
        return ResponseEntity.ok(createdMpa);
    }

    @PutMapping
    public ResponseEntity<Mpa> updateMpa(@RequestBody Mpa mpa) {
        Mpa updatedMpa = this.mpaService.updateMpa(mpa);
        log.info("Mpa updated - {}", updatedMpa.getName());
        return ResponseEntity.ok(updatedMpa);
    }

    @GetMapping()
    public ResponseEntity<?> getAllMpa() {
        List<Mpa> mpaList = this.mpaService.getAllMpa();
        return ResponseEntity.ok(mpaList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mpa> getMpa(@PathVariable long id) {
        Mpa mpa = this.mpaService.getMpa(id);
        return ResponseEntity.ok(mpa);
    }
}
