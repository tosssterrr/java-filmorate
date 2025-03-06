package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserService {
    User createUser(User user);

    Collection<User> getAllUsers();

    User updateUser(User user) throws IdNotFoundException;

    User addFriend(long id, long friendId);

    Set<Long> getUserFriends(long id);

    User deleteFriend(long id, long friendId);

    Set<Long> getCommonFriends(long userId, long otherId);
}
