package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.controller.dto.FilmUpdateDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class DbFilmService implements FilmService {

    private final FilmRepository filmRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    @Override
    public Film createFilm(FilmRequestDto filmDto) {
        Film film = extractDto(filmDto);

        return filmRepository.save(film);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    @Override
    public Film updateFilm(FilmUpdateDto filmDto) {
        Film film = extractDto(filmDto);
        film.setId(filmDto.getId());

        return filmRepository.update(film);
    }

    private Film extractDto(FilmRequestDto filmDto) {
        Mpa mpa = mpaRepository.findById(filmDto.getMpa().getId());

        List<Genre> genres = filmDto.getGenres().stream()
                .map(genreDto -> genreRepository.findById(genreDto.getId()))
                .collect(Collectors.toList());

        Film film = new Film();
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        film.setMpa(mpa);
        film.setGenres(genres);

        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmRepository.findPopular(count);
    }

    @Override
    public void addUserLike(long filmId, long userId) {
        filmRepository.findById(filmId);
        userRepository.findById(userId);

        filmRepository.addLike(filmId, userId);
    }

    @Override
    public void deleteUserLike(long filmId, long userId) {
        filmRepository.findById(filmId);
        userRepository.findById(userId);

        filmRepository.deleteLike(filmId, userId);
    }

    @Override
    public Film getFilmById(long id) {
        Film film = filmRepository.findById(id);
        film.setGenres(genreRepository.findGenresByFilmId(id));
        return film;
    }
}
