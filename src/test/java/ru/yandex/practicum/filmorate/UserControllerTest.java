package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.LoginValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.MemoryUserService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.MemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new MemoryUserService(new MemoryUserStorage());
        userController = new UserController(userService);
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        User user = new User();
        user.setLogin("user1");
        user.setEmail("example@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user).getBody();

        assertNotNull(createdUser);
        assertEquals(user.getLogin(), createdUser.getLogin());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        User user = new User();
        user.setLogin("user1");
        user.setEmail("example@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userService.createUser(user);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setLogin("user1_updated");
        updatedUser.setEmail("example@mail.com");
        updatedUser.setBirthday(LocalDate.of(2000, 1, 1));

        User result = userController.updateUser(updatedUser).getBody();

        assertEquals(result.getLogin(), updatedUser.getLogin());
    }

    @Test
    void getUsers_ShouldReturnListOfUsers() {
        User user = new User();
        user.setLogin("user1");
        user.setEmail("example@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        userService.createUser(user);

        Collection<User> users = userController.getUsers().getBody();
        assertEquals(1, users.size());
        assertEquals(user.getLogin(), users.iterator().next().getLogin());
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserDoesNotExist() {
        User user = new User();
        user.setId(555L);
        user.setLogin("user1");
        user.setEmail("example@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(IdNotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    void createUser_ShouldThrowException_WhenLoginInValid() {
        User user = new User();
        user.setLogin("invalid username");
        user.setEmail("example@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(LoginValidateException.class, () -> userService.createUser(user));
    }
}
