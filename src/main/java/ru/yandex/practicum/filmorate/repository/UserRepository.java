package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<Long, User> {
    private static final String TABLE = "Users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM " + TABLE + " WHERE id = ?";
    private static final String INSERT_SQL =
            "INSERT INTO " + TABLE + " (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE " + TABLE + " SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM " + TABLE;

    private static final String ADD_FRIEND_SQL =
            "INSERT INTO User_friendship (user_id, friend_id, status) VALUES (?, ?, ?)";
    private static final String REMOVE_FRIEND_SQL =
            "DELETE FROM User_friendship WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_COMMON_FRIENDS_SQL =
            "SELECT * FROM Users WHERE id IN (" +
                    "   SELECT friend_id FROM User_friendship WHERE user_id = ? AND status = 'confirmed' " +
                    "   INTERSECT " +
                    "   SELECT friend_id FROM User_friendship WHERE user_id = ? AND status = 'confirmed')";

    private static final String FIND_FRIENDS_SQL =
            "SELECT * FROM Users WHERE id IN (" +
                    "SELECT friend_id FROM User_friendship WHERE user_id = ? AND status = 'confirmed')";

    public UserRepository(JdbcTemplate jdbc) {
        super(jdbc, (rs, rowNum) -> mapUser(rs));
    }

    public User findById(long id) {
        Optional<User> optUser = findOne(FIND_BY_ID_SQL, id);
        if (optUser.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id " + id + " не найден");
        }
        return optUser.get();
    }

    public User save(User user) {
        return insert(INSERT_SQL, ps -> {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
        });
    }

    public User update(User user) {
        if (!super.update(UPDATE_SQL,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId())) {
            throw new IdNotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        return user;
    }

    public List<User> findAll() {
        return findMany(FIND_ALL);
    }

    public void addFriend(long userId, long friendId, String status) {
        update(ADD_FRIEND_SQL, userId, friendId, status);
    }

    public void removeFriend(long userId, long friendId) {
        update(REMOVE_FRIEND_SQL, userId, friendId);
    }

    public List<User> getFriends(long userId) {
        return findMany(FIND_FRIENDS_SQL, userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        return findMany(FIND_COMMON_FRIENDS_SQL, userId, otherId);
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
    protected User loadById(Long id) {
        return findById(id);
    }
}
