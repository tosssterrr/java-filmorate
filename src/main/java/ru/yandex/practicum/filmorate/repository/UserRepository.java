package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<Long, User> {
    private static final String TABLE = "Users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM " + TABLE + " WHERE id = :id";
    private static final String INSERT_SQL =
            "INSERT INTO " + TABLE + " (email, login, name, birthday) VALUES (:email, :login, :name, :birthday)";
    private static final String UPDATE_SQL =
            "UPDATE " + TABLE + " SET email = :email, login = :login, name = :name, birthday = :birthday WHERE id = :id";
    private static final String FIND_ALL = "SELECT * FROM " + TABLE;

    private static final String ADD_FRIEND_SQL =
            "INSERT INTO User_friendship (user_id, friend_id, status) VALUES (:userId, :friendId, :status)";
    private static final String REMOVE_FRIEND_SQL =
            "DELETE FROM User_friendship WHERE user_id = :userId AND friend_id = :friendId";
    private static final String FIND_COMMON_FRIENDS_SQL =
            "SELECT * FROM Users WHERE id IN (" +
                    "   SELECT friend_id FROM User_friendship WHERE user_id = :userId AND status = 'confirmed' " +
                    "   INTERSECT " +
                    "   SELECT friend_id FROM User_friendship WHERE user_id = :otherId AND status = 'confirmed')";

    private static final String FIND_FRIENDS_SQL =
            "SELECT * FROM Users WHERE id IN (" +
                    "SELECT friend_id FROM User_friendship WHERE user_id = :userId AND status = 'confirmed')";

    public UserRepository(NamedParameterJdbcTemplate jdbc) {
        super(jdbc, (rs, rowNum) -> mapUser(rs));
    }

    public User findById(long id) {
        Optional<User> optUser = findOne(FIND_BY_ID_SQL, Map.of("id", id));
        if (optUser.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id " + id + " не найден");
        }
        return optUser.get();
    }

    public User save(User user) {
        Map<String, Object> params = Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        );
        return insert(INSERT_SQL, user, params);
    }

    public User update(User user) {
        Map<String, Object> params = Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        );
        if (!super.update(UPDATE_SQL, params)) {
            throw new IdNotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        return user;
    }

    public List<User> findAll() {
        return findMany(FIND_ALL, Map.of());
    }

    public void addFriend(long userId, long friendId, String status) {
        Map<String, Object> params = Map.of(
                "userId", userId,
                "friendId", friendId,
                "status", status
        );
        update(ADD_FRIEND_SQL, params);
    }

    public void removeFriend(long userId, long friendId) {
        Map<String, Object> params = Map.of(
                "userId", userId,
                "friendId", friendId
        );
        update(REMOVE_FRIEND_SQL, params);
    }

    public List<User> getFriends(long userId) {
        return findMany(FIND_FRIENDS_SQL, Map.of("userId", userId));
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        return findMany(FIND_COMMON_FRIENDS_SQL, Map.of("userId", userId, "otherId", otherId));
    }

    public boolean existsById(long userId) {
        return findById(userId) != null;
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    @Override
    protected Long extractGeneratedKey(KeyHolder keyHolder) {
        return keyHolder.getKey().longValue();
    }

    @Override
    protected void setGeneratedKeyToEntity(User entity, Long key) {
        entity.setId(key);
    }
}
