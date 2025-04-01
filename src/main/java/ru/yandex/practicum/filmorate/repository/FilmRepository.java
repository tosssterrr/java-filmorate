package ru.yandex.practicum.filmorate.repository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Long, Film> {

    private static final String FIND_ALL_SQL =
            "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name, " +
                    "(SELECT COUNT(*) FROM film_likes WHERE film_id = f.id) AS likes_count " +
                    "FROM film f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id";

    private static final String INSERT_SQL =
            "INSERT INTO film (name, description, release_date, duration, mpa_id) " +
                    "VALUES (:name, :description, :releaseDate, :duration, :mpaId)";

    private static final String INSERT_LIKE_SQL =
            "INSERT INTO Film_likes (film_id, user_id) VALUES (:filmId, :userId)";

    private static final String DELETE_LIKE_SQL =
            "DELETE FROM Film_likes WHERE film_id = :filmId AND user_id = :userId";

    private static final String INSERT_GENRE_SQL =
            "INSERT INTO film_genre (film_id, genre_id) VALUES (:filmId, :genreId)";

    private static final String UPDATE_SQL =
            "UPDATE Film SET name = :name, description = :description, " +
                    "release_date = :releaseDate, duration = :duration, mpa_id = :mpaId " +
                    "WHERE id = :id";

    private static final String FIND_BY_ID_SQL =
            "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name, " +
                    "g.id AS genre_id, g.name AS genre_name, " +
                    "(SELECT COUNT(*) FROM film_likes WHERE film_id = f.id) AS likes_count " +
                    "FROM film f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                    "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.id " +
                    "WHERE f.id = :id";

    private static final String FIND_POPULAR_SQL =
            "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name, " +
            "(SELECT COUNT(*) FROM film_likes WHERE film_id = f.id) AS likes_count " +
            "FROM film f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                    "ORDER BY likes_count DESC " +
                    "LIMIT :limit";

    public FilmRepository(NamedParameterJdbcTemplate jdbc) {
        super(jdbc, (rs, rowNum) -> mapFilm(rs));
    }

    @Transactional
    public Film save(Film film) {
        Map<String, Object> params = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "duration", film.getDuration(),
                "releaseDate", film.getReleaseDate(),
                "mpaId", film.getMpa().getId()
        );
        Film savedFilm = insert(INSERT_SQL, film, params);
        List<Genre> sortedGenres = film.getGenres().stream()
                .sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toList());
        film.setGenres(sortedGenres);
        saveGenres(savedFilm.getId(), film.getGenres());
        return savedFilm;
    }

    @Transactional
    public Film update(Film film) {
        Map<String, Object> params = Map.of(
                "id", film.getId(),
                "name", film.getName(),
                "description", film.getDescription(),
                "duration", film.getDuration(),
                "releaseDate", film.getReleaseDate(),
                "mpaId", film.getMpa().getId()
        );
        if (!super.update(UPDATE_SQL, params)) {
            throw new IdNotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        return film;
    }

    private void saveGenres(Long filmId, List<Genre> genres) {
        genres.forEach(genre -> {
            Map<String, Object> params = Map.of(
                    "filmId", filmId,
                    "genreId", genre.getId()
            );
            update(INSERT_GENRE_SQL, params);
        });
    }

    public Film findById(long id) {
        return jdbc.query(FIND_BY_ID_SQL, Map.of("id", id), rs -> {
            Film film = null;
            List<Genre> genres = new ArrayList<>();

            while (rs.next()) {
                if (film == null) {
                    film = new Film();
                    film.setId(rs.getLong("id"));
                    film.setName(rs.getString("name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getInt("duration"));


                    Mpa mpa = new Mpa();
                    mpa.setId(rs.getLong("mpa_id"));
                    mpa.setName(rs.getString("mpa_name"));
                    film.setMpa(mpa);
                }

                long genreId = rs.getLong("genre_id");
                if (!rs.wasNull()) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    genres.add(genre);
                }
            }

            if (film == null) {
                throw new IdNotFoundException("Фильм с ID " + id + " не найден");
            }

            film.setGenres(genres);
            return film;
        });
    }

    private static Film mapFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setLikesCount(rs.getInt("likes_count"));


        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        film.setGenres(new ArrayList<>());

        return film;
    }

    @Override
    protected Long extractGeneratedKey(KeyHolder keyHolder) {
        return keyHolder.getKey().longValue();
    }

    @Override
    protected void setGeneratedKeyToEntity(Film entity, Long key) {
        entity.setId(key);
    }


    public void addLike(long filmId, long userId) {
        Map<String, Object> params = Map.of(
                "filmId", filmId,
                "userId", userId
        );
        update(INSERT_LIKE_SQL, params);
    }

    public void deleteLike(long filmId, long userId) {
        Map<String, Object> params = Map.of(
                "filmId", filmId,
                "userId", userId
        );
        update(DELETE_LIKE_SQL, params);
    }

    public Collection<Film> findAll() {
        return findMany(FIND_ALL_SQL, Map.of());
    }

    public List<Film> findPopular(int count) {
        return findMany(FIND_POPULAR_SQL, Map.of("limit", count));
    }
}
