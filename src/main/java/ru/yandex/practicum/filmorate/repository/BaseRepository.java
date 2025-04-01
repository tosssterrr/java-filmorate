package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseRepository<K, T> {
    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Map<String, Object> params) {
        try {
            SqlParameterSource paramSource = new MapSqlParameterSource(params);
            T result = jdbc.queryForObject(query, paramSource, mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<T> findMany(String query, Map<String, Object> params) {
        SqlParameterSource paramSource = new MapSqlParameterSource(params);
        return jdbc.query(query, paramSource, mapper);
    }

    public boolean update(String query, Map<String, Object> params) {
        SqlParameterSource paramSource = new MapSqlParameterSource(params);
        return jdbc.update(query, paramSource) > 0;
    }

    protected T insert(String sql, T entity, Map<String, Object> params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, new MapSqlParameterSource(params), keyHolder);

        K generatedKey = extractGeneratedKey(keyHolder);
        setGeneratedKeyToEntity(entity, generatedKey);
        return entity;
    }

    protected abstract K extractGeneratedKey(KeyHolder keyHolder);
    protected abstract void setGeneratedKeyToEntity(T entity, K key);
}
