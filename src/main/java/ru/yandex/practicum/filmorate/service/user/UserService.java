package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    User createUser(User user);

    Collection<User> getAllUsers();

    User updateUser(User user) throws IdNotFoundException;
}
