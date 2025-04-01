package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserRepositoryTest extends BaseTest {

    @Test
    public void testFindUserById() {
        User user = userRepository.findById(1L);
        assertNotNull(user);
        assertEquals("user1@example.com", user.getEmail());
        assertEquals("user1", user.getLogin());
    }

    @Test
    public void testCreateUser() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setLogin("new_user");
        newUser.setName("New Name");
        newUser.setBirthday(LocalDate.of(1, 1, 1));

        User savedUser = userRepository.save(newUser);
        assertNotNull(savedUser);
        assertEquals("new@example.com", savedUser.getEmail());
    }
}