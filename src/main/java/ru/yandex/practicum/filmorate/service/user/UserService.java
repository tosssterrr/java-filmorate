package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserService {
    User createUser(User user);

    Collection<User> getAllUsers();

    User updateUser(User user) throws IdNotFoundException;

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);

    Set<User> getCommonFriends(long userId, long otherId);

    User getUser(long id);

    List<User> getFriends(long id);
}
