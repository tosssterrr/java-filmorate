package ru.yandex.practicum.filmorate.repository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Long, Film> {

    private static final String BASE_SQL =
            "SELECT f.*, " +
                    "       m.id AS mpa_id, m.name AS mpa_name, " +
                    "       g.id AS genre_id, g.name AS genre_name, " +
                    "       (SELECT COUNT(*) FROM film_likes WHERE film_id = f.id) AS likes_count " +
                    "FROM film f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                    "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.id ";

    private static final String FIND_ALL_SQL = BASE_SQL;

    private static final String FIND_BY_ID_SQL = BASE_SQL + "WHERE f.id = ?";

    private static final String FIND_POPULAR_SQL = BASE_SQL + "ORDER BY likes_count DESC LIMIT ?";

    private static final String INSERT_SQL =
            "INSERT INTO film (name, description, release_date, duration, mpa_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_LIKE_SQL =
            "INSERT INTO Film_likes (film_id, user_id) VALUES (?, ?)";

    private static final String DELETE_LIKE_SQL =
            "DELETE FROM Film_likes WHERE film_id = ? AND user_id = ?";

    private static final String INSERT_GENRE_SQL =
            "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE Film SET name = ?, description = ?, " +
                    "release_date = ?, duration = ?, mpa_id = ? " +
                    "WHERE id = ?";

    public FilmRepository(JdbcTemplate jdbc) {
        super(jdbc, (rs, rowNum) -> mapFilm(rs));
    }


    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL_SQL, this::extractFilms);
    }

    public Film save(Film film) {
        Film savedFilm = insert(INSERT_SQL, ps -> {
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
        });

        List<Genre> sortedGenres = film.getGenres().stream()
                .sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toList());
        savedFilm.setGenres(sortedGenres);
        saveGenres(savedFilm.getId(), savedFilm.getGenres());
        return savedFilm;
    }

    @Transactional
    public Film update(Film film) {
        if (!super.update(UPDATE_SQL,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId())) {
            throw new IdNotFoundException("Фильм с ID " + film.getId() + " не найден");
        }

        updateGenres(film.getId(), film.getGenres());

        return loadById(film.getId());
    }

    private void updateGenres(Long filmId, List<Genre> newGenres) {
        // Удаляем старые жанры
        jdbc.update("DELETE FROM film_genre WHERE film_id = ?", filmId);

        // Добавляем новые
        if (!newGenres.isEmpty()) {
            jdbc.batchUpdate(
                    "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, filmId);
                            ps.setLong(2, newGenres.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return newGenres.size();
                        }
                    }
            );
        }
    }

    public Film findById(long id) {
        return jdbc.query(
                FIND_BY_ID_SQL,
                new Object[]{id},
                rs -> {
                    if (!rs.next()) {
                        throw new IdNotFoundException("Фильм с ID " + id + " не найден");
                    }
                    return mapFilm(rs);
                }
        );
    }

    public List<Film> findPopular(int count) {
        return jdbc.query(
                FIND_POPULAR_SQL,
                new Object[]{count},
                this::extractFilms
        );
    }

    private void saveGenres(Long filmId, List<Genre> genres) {
        jdbc.batchUpdate(INSERT_GENRE_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genres.get(i);
                ps.setLong(1, filmId);
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    public void addLike(long filmId, long userId) {
        update(INSERT_LIKE_SQL, filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        update(DELETE_LIKE_SQL, filmId, userId);
    }

    private List<Film> extractFilms(ResultSet rs) throws SQLException {
        Map<Long, Film> films = new LinkedHashMap<>();

        while (rs.next()) {
            Long filmId = rs.getLong("id");

            films.computeIfAbsent(filmId, id -> {
                try {
                    return mapFilmWithoutGenres(rs);
                } catch (SQLException e) {
                    throw new RuntimeException("Ошибка маппинга фильма", e);
                }
            });

            if (rs.getLong("genre_id") != 0) {
                Genre genre = mapGenre(rs);
                films.get(filmId).getGenres().add(genre);
            }
        }

        return new ArrayList<>(films.values());
    }

    private static Film mapFilmWithoutGenres(ResultSet rs) throws SQLException {
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

        return film;
    }

    private Genre mapGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("genre_id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }

    private static Film mapFilm(ResultSet rs) throws SQLException {
        Film film = mapFilmWithoutGenres(rs);

        List<Genre> genres = new ArrayList<>();
        do {
            long genreId = rs.getLong("genre_id");
            if (!rs.wasNull()) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                genres.add(genre);
            }
        } while (rs.next());

        film.setGenres(genres);
        return film;
    }

    @Override
    protected Long extractGeneratedKey(KeyHolder keyHolder) {
        return keyHolder.getKey().longValue();
    }

    @Override
    protected Film loadById(Long id) {
        return findById(id);
    }
}
