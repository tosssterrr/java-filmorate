package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FriendDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<Long> friendIds = userService.getCommonFriends(id, friendId);
        log.info("Common friends - {}", friendIds);

        List<FriendDto> friends =  friendIds.stream()
                .map(FriendDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(friends);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable long id, @PathVariable long friendId) {
        User user = userService.addFriend(id, friendId);
        log.info("Friend added, friends amount - {}", user.getFriends().size());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        User user = userService.deleteFriend(id, friendId);
        log.info("Friend deleted, friends amount - {}", user.getFriends().size());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<FriendDto>> getFriends(@PathVariable long id) {
        User user = userService.getUser(id);
        Set<Long> friendIds = user.getFriends();

        // Преобразуем список ID в список DTO с полем "id"
        List<FriendDto> friends = friendIds.stream()
                .map(FriendDto::new)
                .collect(Collectors.toList());

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
