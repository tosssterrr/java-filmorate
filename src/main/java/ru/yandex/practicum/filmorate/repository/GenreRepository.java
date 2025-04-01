package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GenreRepository extends BaseRepository<Long, Genre> {

    private static final String INSERT_SQL = "INSERT INTO Genre (name) VALUES (:name)";
    private static final String UPDATE_SQL = "UPDATE Genre SET name = :name WHERE id = :id";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM Genre WHERE id = :id";
    private static final String FIND_ALL = "SELECT * FROM Genre ORDER BY id";
    private static final String FIND_BY_FILM_ID_SQL =
            "SELECT g.* FROM genre g " +
            "JOIN film_genre fg ON g.id = fg.genre_id " +
            "WHERE fg.film_id = :filmId " +
                    "ORDER BY g.id ASC";

    public GenreRepository(NamedParameterJdbcTemplate jdbc) {
        super(jdbc, (rs, rowNum) -> mapGenre(rs));
    }

    public Genre save(Genre genre) {
        Map<String, Object> params = Map.of(
                "name", genre.getName()
        );
        return insert(INSERT_SQL, genre, params);
    }

    public Genre update(Genre genre) {
        Map<String, Object> params = Map.of(
                "id", genre.getId(),
                "name", genre.getName()
        );
        if (!super.update(UPDATE_SQL, params)) {
            throw new IdNotFoundException("Жанр с ID " + genre.getId() + " не найден");
        }
        return genre;
    }

    public Genre findById(long id) {
        Optional<Genre> optGenre = findOne(FIND_BY_ID_SQL, Map.of("id", id));
        if (optGenre.isEmpty()) {
            throw new IdNotFoundException("Жанр с ID " + id + " не найден");
        }
        return optGenre.get();
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL, Map.of());
    }

    public List<Genre> findGenresByFilmId(long filmId) {
        return new ArrayList<>(findMany(FIND_BY_FILM_ID_SQL, Map.of("filmId", filmId)));
    }

    private static Genre mapGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    @Override
    protected Long extractGeneratedKey(KeyHolder keyHolder) {
        return keyHolder.getKey().longValue();
    }

    @Override
    protected void setGeneratedKeyToEntity(Genre entity, Long key) {
        entity.setId(key);
    }
}
