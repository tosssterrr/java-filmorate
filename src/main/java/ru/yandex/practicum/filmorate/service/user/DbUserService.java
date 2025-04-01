package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Primary
public class DbUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) throws IdNotFoundException {
        return userRepository.update(user);
    }

    @Override
    public void addFriend(long id, long friendId) {
        validateUserExists(id);
        validateUserExists(friendId);

        userRepository.addFriend(id, friendId, "confirmed");
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        userRepository.removeFriend(userId, friendId);
    }

    @Override
    public Set<User> getCommonFriends(long userId, long otherId) {
        return Set.copyOf(userRepository.getCommonFriends(userId, otherId));
    }

    @Override
    public User getUser(long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getFriends(long id) {
        validateUserExists(id);
        return userRepository.getFriends(id);
    }

    private void validateUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IdNotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }
}
