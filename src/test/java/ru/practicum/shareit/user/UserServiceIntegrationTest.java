package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareitService",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();

    UserDto userDto3 = UserDto.builder()
            .id(1L)
            .name("user1updated")
            .email("user1@email.ru")
            .build();


    @Test
    void testUpdateUser() {

        userService.addUserToStorage(userDto1);

        userService.updateUser(userDto3, userDto3.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);

        User user = query
                .setParameter("id", userDto3.getId())
                .getSingleResult();

        assertThat(user.getName(), equalTo(userDto3.getName()));

        NotFoundException exception1 = assertThrows(NotFoundException.class, () -> userService.getUserById(100L));

        assertEquals(NotFoundException.class, exception1.getClass());
        assertEquals("Пользователь с индексом 100 не найден в базе.", exception1.getMessage());

        NotFoundException exception2 = assertThrows(NotFoundException.class, () -> userService.updateUser(userDto3, 100L));

        assertEquals(NotFoundException.class, exception2.getClass());
        assertEquals("Пользователь с индексом 100 не найден в базе.", exception1.getMessage());
    }
}