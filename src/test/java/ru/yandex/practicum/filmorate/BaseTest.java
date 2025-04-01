package ru.yandex.practicum.filmorate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserRepository.class, FilmRepository.class, GenreRepository.class, MpaRepository.class})
@Sql({"/schema.sql", "/data.sql"})
public abstract class BaseTest {
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected FilmRepository filmRepository;

    @Autowired
    protected GenreRepository genreRepository;

    @Autowired
    protected MpaRepository mpaRepository;
}