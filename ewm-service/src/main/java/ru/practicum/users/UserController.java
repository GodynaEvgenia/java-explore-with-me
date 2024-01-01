package ru.practicum.users;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.ResourceConflictException;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Получить всех пользователей
    @GetMapping
    public ResponseEntity<List<User>> getUsersByIds(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        List<User> users;
        if (ids == null || ids.isEmpty()) {
            // Если ids не переданы, получить всех пользователей с прокруткой
            users = userService.getAllUsers(from, size);
        } else {
            users = userService.getUsersByIds(ids, from, size);
        }
        return ResponseEntity.ok(users);
    }

    // Добавить пользователя (возвращаем 201 при успешном создании)
    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (ResourceConflictException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(user);
        }
    }

    // Удалить пользователя по id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
