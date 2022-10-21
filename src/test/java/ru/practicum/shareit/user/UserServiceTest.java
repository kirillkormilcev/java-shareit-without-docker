package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.error.exception.IncorrectRequestParamException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserServiceTest {

    UserRepository userRepository = mock(UserRepository.class);
    UserService userService = new UserService(userRepository);

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();
    UserDto userDto1 = UserDto.builder()
            .id(1L)
                .name("user1")
                .email("user1@email.ru")
                .build();


    @Test
    void testUserService() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        assertEquals(userService.addUserToStorage(userDto1).getId(), userDto1.getId());

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));

        assertEquals(userService.getUserById(1L).getId(), userDto1.getId());

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user1));

        assertEquals(userService.getAllUsers().get(0).getId(), userDto1.getId());

        assertEquals(userService.updateUser(userDto1, 1L).getId(), userDto1.getId());

        userService.deleteUserById(user1.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(user1.getId());

        Mockito.when(userRepository.findById(100L)).thenThrow(NotFoundException.class);

        NotFoundException exception1 = assertThrows(NotFoundException.class, () -> userService.getUserById(100L));

        assertEquals(NotFoundException.class, exception1.getClass());

        NotFoundException exception2 = assertThrows(NotFoundException.class, () -> userService.updateUser(userDto1,100L));

        assertEquals(NotFoundException.class, exception2.getClass());

        IncorrectRequestParamException exception3 = assertThrows(IncorrectRequestParamException.class, () -> userService.updateUser(null,user1.getId()));

        assertEquals(IncorrectRequestParamException.class, exception3.getClass());
    }
}