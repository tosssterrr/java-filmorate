package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    User save(User user);

    User update(User user);

    Collection<User> findAll();

    User findById(long id);

    <T> Set<T> findCommonFriends(Set<T> friends1, Set<T> friends2);
}
