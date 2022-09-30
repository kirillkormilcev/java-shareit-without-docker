package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RequestControllerTest {

    RequestService requestService = Mockito.mock(RequestService.class);

    private final RequestController requestController = new RequestController(requestService);

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(requestController)
            .build();

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();

    UserDto userDto1 = UserDto.builder()
            .id(21)
            .name("user1")
            .email("user1@email.ru")
            .build();

    Request request1 = Request.builder()
            .id(1L)
            .description("r_desc")
            .requestor(user1)
            .build();

    RequestDtoIn requestDtoIn1 = RequestDtoIn.builder()
            .id(1L)
            .description("r_desc")
            .build();

    RequestDtoOut requestDtoOut1 = RequestDtoOut.builder()
            .id(1L)
            .description("r_desc")
            .requestor(userDto1)
            .build();

    @Test
    void testRequestControllerTest() throws Exception {

        Mockito.when(requestService.addRequest(Mockito.any(RequestDtoIn.class), Mockito.anyLong()))
                .thenReturn(requestDtoOut1);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDtoIn1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOut1.getId()), long.class));

        Mockito.when(requestService.getRequestsByUserId(Mockito.anyLong()))
                .thenReturn(List.of(requestDtoOut1));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.when(requestService.getRequestsOfOtherUsers(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(requestDtoOut1));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.when(requestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(requestDtoOut1);

        mvc.perform(get("/requests/" + request1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOut1.getId()), long.class));
    }
}