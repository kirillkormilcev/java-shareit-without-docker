package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    User user1 = User.builder()
            .id(1L)
            .email("user1@email.ru")
            .build();
    User user2 = User.builder()
            .id(2L)
            .name("user1")
            .build();

    @Test
    void testUserMapper() {

        UserMapper.updateNotNullField(user1, user1);

        assertNull(user1.getName());

        UserMapper.updateNotNullField(user2, user2);

        assertNull(user2.getEmail());

        UserMapper userMapper = new UserMapper();
    }

}