package ru.practicum.ewm.mainservice.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.users.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/users")
public class AdminUsersController {

    private final AdminUsersService usersService;

    @GetMapping
    public ResponseEntity<Object> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                           @Valid @RequestParam(name = "from", required = false, defaultValue = 0 + "")
                                                  @PositiveOrZero(message = "Параметр запроса 'from' должен быть больше либо равен 0") Integer from,
                                           @Valid @RequestParam(name = "size", required = false, defaultValue = 10 + "")
                                                  @Positive(message = "Параметр запроса 'size' должен быть больше 0") Integer size) {
        log.info("Запрос на получение списка пользователей с параметрами: ids = {}, from = {}, size = {}", ids, from, size);
        return new ResponseEntity<>(usersService.getUsers(ids, from, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на создание нового пользователя с параметрами: name = {}, email = {}", userDto.getName(), userDto.getEmail());
        return new ResponseEntity<>(usersService.createUser(userDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Valid @PathVariable("userId")
                                                    @NotNull(message = "Значение 'userId' не может быть равно null")
                                                    @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId) {
        log.info("Запрос на удаление данных пользователей с id = {}", userId);
        usersService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
