package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerRestTest {

    UserService userService = Mockito.mock(UserService.class);

    private final UserController userController = new UserController(userService);

    private final ObjectMapper mapper = new ObjectMapper();

    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(userController)
            .build();

    UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();

    @Test
    void testUserControllerRest() throws Exception {

        Mockito.when(userService.addUserToStorage(Mockito.any(UserDto.class))).thenReturn(userDto1);

        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(userDto1))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class));

        userDto1.setName("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        userDto1.setName("user1");
        userDto1.setEmail("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        userDto1.setEmail("user1@email.ru");

        Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(userDto1);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class));

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(userDto1));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}