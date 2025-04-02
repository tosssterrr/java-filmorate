package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GenreRepository extends BaseRepository<Long, Genre> {

    private static final String INSERT_SQL = "INSERT INTO Genre (name) VALUES (?)";
    private static final String UPDATE_SQL = "UPDATE Genre SET name = :name WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM Genre WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM Genre ORDER BY id";
    private static final String FIND_BY_FILM_ID_SQL =
            "SELECT g.* FROM genre g " +
            "JOIN film_genre fg ON g.id = fg.genre_id " +
            "WHERE fg.film_id = ? " +
                    "ORDER BY g.id ASC";

    public GenreRepository(JdbcTemplate jdbc) {
        super(jdbc, (rs, rowNum) -> mapGenre(rs));
    }

    public Genre save(Genre genre) {
        return insert(INSERT_SQL, ps ->
                ps.setString(1, genre.getName()));
    }

    public Genre update(Genre genre) {
        if (!super.update(UPDATE_SQL, genre.getId(), genre.getName())) {
            throw new IdNotFoundException("Жанр с ID " + genre.getId() + " не найден");
        }
        return genre;
    }

    public Genre findById(long id) {
        Optional<Genre> optGenre = findOne(FIND_BY_ID_SQL, id);
        if (optGenre.isEmpty()) {
            throw new IdNotFoundException("Жанр с ID " + id + " не найден");
        }
        return optGenre.get();
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL);
    }

    public List<Genre> findGenresByFilmId(long filmId) {
        return new ArrayList<>(findMany(FIND_BY_FILM_ID_SQL, filmId));
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
    protected Genre loadById(Long id) {
        return findById(id);
    }
}
