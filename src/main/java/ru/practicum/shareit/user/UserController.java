package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.error.validation.Create;
import ru.practicum.shareit.error.validation.Update;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Validated({Create.class}) @RequestBody UserDto userDto) { //todo validated
        log.info("Обработка эндпойнта POST /users.");
        return new ResponseEntity<>(userService.addUserToStorage(userDto), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        log.info("Обработка эндпойнта GET /users/" + userId + ".");
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Обработка эндпойнта GET /users.");
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable long userId,
                                              @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Обработка эндпойнта PATCH /users/" + userId + ".");
        return new ResponseEntity<>(userService.updateUser(userDto, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public HttpStatus deleteUser(@PathVariable long userId) {
        log.info("Обработка эндпойнта DELETE /users/" + userId + ".");
        userService.deleteUserById(userId);
        return HttpStatus.OK;
    }
}
