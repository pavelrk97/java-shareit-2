package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.utils.Marker;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @Validated(Marker.OnCreate.class)
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("POST запрос на создание пользователя: {}", userRequestDto);
        return userClient.create(userRequestDto);
    }

    @Validated(Marker.OnUpdate.class)
    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody UserRequestDto userDto, @PathVariable Long id) {
        log.info("PATCH запрос на обновление пользователя c id: {}", id);
        return userClient.update(id, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("GET запрос на получение списка всех пользователей.");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        log.info("GET запрос на получение пользователя c id: {}", id);
        return userClient.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("DELETE запрос на удаление пользователя с id: {}", id);
        return userClient.deleteById(id);
    }
}