package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public ResponseEntity<?> getCommonFriends(@PathVariable long id, @PathVariable long friendId) {
        Set<Long> friends = userService.getCommonFriends(id, friendId);
        log.info("Common friends - {}", friends);
        return ResponseEntity.ok(Map.of("friends", friends));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable long id, @PathVariable long friendId) {
        User user = userService.addFriend(id, friendId);
        log.info("Friend added, friends amount - {}", user.getFriends().size());
        return ResponseEntity.ok(Map.of("friends", user.getFriends()));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        User user = userService.deleteFriend(id, friendId);
        log.info("Friend deleted, friends amount - {}", user.getFriends().size());
        return ResponseEntity.ok(Map.of("friends", user.getFriends()));
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<Long>> getUserFriends(@PathVariable long id) {
        Set<Long> friends = userService.getUserFriends(id);
        return ResponseEntity.ok(friends);
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        Collection<User> users = this.userService.getAllUsers();
        log.info("Get all users - {}", users.size());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = this.userService.createUser(user);
        log.info("User created - {}", createdUser);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        User updatedUser = this.userService.updateUser(user);
        log.info("User updated - {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }
}
