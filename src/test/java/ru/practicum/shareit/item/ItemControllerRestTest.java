package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
class ItemControllerRestTest {

    ItemService itemService = Mockito.mock(ItemService.class);

    private final ItemController itemController = new ItemController(itemService);

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(itemController)
            .build();

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();

    Item item1 = Item.builder()
            .id(1L)
            .name("item1")
            .description("i_desc1")
            .available(true)
            .owner(user1)
            .build();

    ItemDtoIn itemDtoIn1 = ItemDtoIn.builder()
            .id(1L)
            .name("item1")
            .description("i_desc1")
            .available(true)
            .build();

    ItemDtoOut itemDtoOut1 = ItemDtoOut.builder()
            .id(1L)
            .name("item1")
            .description("i_desc1")
            .available(true)
            .build();

    CommentDto commentDto1 = CommentDto.builder()
            .id(1L)
            .text("comment1")
            .item(itemDtoOut1)
            .authorName(user1.getName())
            .created(LocalDateTime.now())
            .build();

    @Test
    void testItemControllerRest() throws Exception {

        Mockito.when(itemService.addItem(Mockito.any(ItemDtoIn.class), Mockito.anyLong())).thenReturn(itemDtoOut1);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoIn1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut1.getId()), long.class));

        Mockito.when(itemService.updateItem(Mockito.any(ItemDtoIn.class), Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDtoOut1);

        mvc.perform(patch("/items/" + item1.getId())
                        .content(mapper.writeValueAsString(itemDtoIn1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut1.getId()), long.class));

        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDtoOut1);

        mvc.perform(get("/items/" + item1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut1.getId()), long.class));

        Mockito.when(itemService.getItemsByUserId(user1.getId(), 0, 10)).thenReturn(List.of(itemDtoOut1));

        mvc.perform(get("/items/")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.when(itemService.searchAvailableItemsByPartOfNameOrDescription(Mockito.anyString(), Mockito.anyLong(),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(itemDtoOut1));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .param("text", "item1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(CommentDto.class))).thenReturn(commentDto1);

        mvc.perform(post("/items/" + item1.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto1.getId()), long.class));
    }
}