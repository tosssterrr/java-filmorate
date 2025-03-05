package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.LoginValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MemoryUserService implements UserService {

    private final UserStorage storage;

    @Override
    public User createUser(User user) {
        validateUser(user);
        return storage.save(user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return storage.findAll();
    }

    @Override
    public User updateUser(User user) {
        validateUser(user);
        if (storage.findById(user.getId()) == null) {
            throw new IdNotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        return storage.update(user);
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new LoginValidateException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new LoginValidateException("Логин не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin()); // имя пустое используем логин
        }
    }
}
