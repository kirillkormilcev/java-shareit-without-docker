package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserControllerTest {

    UserService userService = mock(UserService.class);
    UserController userController = new UserController(userService);

    UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();

    @Test
    void testUserController() {
        Mockito.when(userService.addUserToStorage(Mockito.any(UserDto.class))).thenReturn(userDto1);

        assertEquals(Objects.requireNonNull(userController.addUser(userDto1).getBody()).getId(), userDto1.getId());

        Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(userDto1);

        assertEquals(Objects.requireNonNull(userController.getUser(userDto1.getId()).getBody()).getId(), userDto1.getId());

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(userDto1));

        assertEquals(Objects.requireNonNull(userController.getAllUsers().getBody()).size(), List.of(userDto1).size());

        Mockito.when(userService.updateUser(Mockito.any(UserDto.class), Mockito.anyLong())).thenReturn(userDto1);

        assertEquals(Objects.requireNonNull(userController.updateUser(userDto1.getId(), userDto1).getBody()).getId(), userDto1.getId());

        assertEquals(userController.deleteUser(userDto1.getId()), HttpStatus.OK);

        Mockito.verify(userService, Mockito.times(1)).deleteUserById(Mockito.anyLong());
    }
}