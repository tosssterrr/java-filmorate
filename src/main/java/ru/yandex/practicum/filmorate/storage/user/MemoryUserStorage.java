package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class MemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private int counter = 0;

    private long getNextId() {
        return ++counter;
    }

    @Override
    public User save(User user) {
        user.setFriends(new HashSet<>());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(long id) {
        if (!users.containsKey(id)) {
            throw new IdNotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public <T> Set<T> findCommonFriends(Set<T> friends1, Set<T> friends2) {
        Set<T> common = new HashSet<>(friends1);
        common.retainAll(friends2); // оставляем только элементы, присутствующие во втором множестве
        return common;
    }
}
