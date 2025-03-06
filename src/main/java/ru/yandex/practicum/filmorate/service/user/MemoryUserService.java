package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LoginValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;

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
        storage.findById(user.getId());
        return storage.update(user);
    }

    @Override
    public User addFriend(long id, long friendId) {
        User user = storage.findById(id);
        User friend = storage.findById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        return user;
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        User user = storage.findById(id);
        User friend = storage.findById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        return user;
    }

    @Override
    public Set<Long> getCommonFriends(long userId, long otherId) {
        return storage.findCommonFriends(storage.findById(userId).getFriends(),
                storage.findById(otherId).getFriends());
    }

    @Override
    public User getUser(long id) {
        return storage.findById(id);
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
