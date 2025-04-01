package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<Long, Mpa> {
    private static final String INSERT_SQL = "INSERT INTO Mpa (name) VALUES (:name)";
    private static final String UPDATE_SQL = "UPDATE Mpa SET name = :name WHERE id = :id";
    private static final String FIND_ALL = "SELECT * FROM Mpa ORDER BY id";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM Mpa WHERE id = :id";

    public MpaRepository(NamedParameterJdbcTemplate jdbc) {
        super(jdbc, (rs, rowNum) -> mapMpa(rs));
    }

    private static Mpa mapMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }

    public Mpa save(Mpa mpa) {
        Map<String, Object> params = Map.of(
                "name", mpa.getName()
        );
        return insert(INSERT_SQL, mpa, params);
    }

    public Mpa update(Mpa mpa) {
        Map<String, Object> params = Map.of(
                "id", mpa.getId(),
                "name", mpa.getName()
        );
        if (!super.update(UPDATE_SQL, params)) {
            throw new IdNotFoundException("Mpa с ID " + mpa.getId() + " не найден");
        }
        return mpa;
    }

    public Mpa findById(long id) {
        Optional<Mpa> optMpa = findOne(FIND_BY_ID_SQL, Map.of("id", id));
        if (optMpa.isEmpty()) {
            throw new IdNotFoundException("Mpa с id " + id + " не найден");
        }
        return optMpa.get();
    }

    public List<Mpa> findAll() {
        return findMany(FIND_ALL, Map.of());
    }

    @Override
    protected Long extractGeneratedKey(KeyHolder keyHolder) {
        return keyHolder.getKey().longValue();
    }

    @Override
    protected void setGeneratedKeyToEntity(Mpa entity, Long key) {
        entity.setId(key);
    }
}
