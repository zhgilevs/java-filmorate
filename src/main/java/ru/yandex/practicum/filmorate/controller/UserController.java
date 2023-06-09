package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    User create(@RequestBody @Valid User user) {
        return service.create(user);
    }

    @PutMapping
    User update(@RequestBody @Valid User updatedUser) {
        return service.update(updatedUser);
    }

    @GetMapping
    List<User> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    User getById(@PathVariable(value = "id") int id) {
        return service.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    User addToFriends(@PathVariable int id,
                      @PathVariable int friendId) {
        return service.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    User removeFromFriends(@PathVariable int id,
                           @PathVariable int friendId) {
        return service.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    List<User> getFriendList(@PathVariable int id) {
        return service.getFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    List<User> getCommonFriends(@PathVariable int id,
                                @PathVariable int otherId) {
        return service.getCommonFriends(id, otherId);
    }
}