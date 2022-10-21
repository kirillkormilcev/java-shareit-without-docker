package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.exception.IncorrectStatusException;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingControllerRestTest {

    BookingService bookingService = Mockito.mock(BookingService.class);

    private final BookingController bookingController = new BookingController(bookingService);

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(bookingController)
            .build();

    ItemDtoOut itemDtoOut1 = ItemDtoOut.builder()
            .id(1L)
            .name("item1")
            .description("i_desc1")
            .available(true)
            .build();

    UserDto userDto2 = UserDto.builder()
            .id(2L)
            .name("user2")
            .email("user2@email.ru")
            .build();

    BookingDtoIn bookingDtoIn1 = BookingDtoIn.builder()
            .start(LocalDateTime.of(2022, 11, 15, 10, 0, 0))
            .ending(LocalDateTime.of(2022, 11, 16, 10, 0, 0))
            .itemId(1L)
            .status(Status.WAITING)
            .build();

    BookingDtoOut bookingDtoOut1 = BookingDtoOut.builder()
            .id(1L)
            .start(LocalDateTime.of(2022, 11, 15, 10, 0, 0))
            .ending(LocalDateTime.of(2022, 11, 16, 10, 0, 0))
            .item(itemDtoOut1)
            .booker(userDto2)
            .status(Status.WAITING)
            .build();

    @Test
    void testBookingControllerRest() throws Exception {

        Mockito.when(bookingService.addBooking(Mockito.any(BookingDtoIn.class), Mockito.anyLong()))
                .thenReturn(bookingDtoOut1);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoIn1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto2.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut1.getId()), long.class));

        Mockito.when(bookingService.updateBookingStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(bookingDtoOut1);

        bookingDtoOut1.setStatus(Status.APPROVED);
        mvc.perform(patch("/bookings/" + bookingDtoOut1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto2.getId())
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(bookingDtoOut1.getStatus().toString()), String.class));

        Mockito.when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDtoOut1);

        mvc.perform(get("/bookings/" + bookingDtoOut1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto2.getId())
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut1.getId()), long.class));

        Mockito.when(bookingService.getBookingsByUserId(State.ALL, userDto2.getId(), 0, 10))
                .thenReturn(List.of(bookingDtoOut1));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto2.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.when(bookingService.getBookingsByOwnerId(State.ALL, userDto2.getId(), 0, 10))
                .thenReturn(List.of(bookingDtoOut1));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto2.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String state = "WRONG";
        IncorrectStatusException ex = assertThrows(IncorrectStatusException.class, () ->
                bookingController.getAllBookingOfUser(state, userDto2.getId(),0, 10));

        assertEquals(IncorrectStatusException.class, ex.getClass());
        assertEquals("При запросе собственником бронирований его вещей указан не " +
                "верный параметр state = " + state + ".", ex.getMessage());
    }
}