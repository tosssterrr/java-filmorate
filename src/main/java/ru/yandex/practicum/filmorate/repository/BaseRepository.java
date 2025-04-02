package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseRepository<K, T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    public Optional<T> findOne(String sql, Object... args) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, mapper, args));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<T> findMany(String query, Object... args) {
        return jdbc.query(query, mapper, args);
    }

    public boolean update(String sql, Object... args) {
        return jdbc.update(sql, args) > 0;
    }

    public T insert(String sql, PreparedStatementSetter pss) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    pss.setValues(ps);
                    return ps;
                },
                keyHolder
        );

        K generatedId = extractGeneratedKey(keyHolder);
        return loadById(generatedId);
    }

    protected abstract K extractGeneratedKey(KeyHolder keyHolder);

    protected abstract T loadById(K id);
}
