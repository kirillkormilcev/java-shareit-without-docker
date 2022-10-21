package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BookingServiceTest {

    BookingRepository bookingRepository = mock(BookingRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    ItemRepository itemRepository = mock(ItemRepository.class);

    BookingService bookingService = new BookingService(bookingRepository, userRepository, itemRepository);

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@email.ru")
            .build();

    UserDto userDto2 = UserDto.builder()
            .id(2L)
            .name("user2")
            .email("user2@email.ru")
            .build();

    Item item1 = Item.builder()
            .id(1L)
            .name("item1")
            .description("i_desc1")
            .available(true)
            .owner(user1)
            .build();

    ItemDtoOut itemDtoOut1 = ItemDtoOut.builder()
            .id(1L)
            .name("item1")
            .description("i_desc1")
            .available(true)
            .build();

    Booking booking1 = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2022, 10, 15, 10, 0, 0))
            .ending(LocalDateTime.of(2022, 10, 16, 10, 0, 0))
            .item(item1)
            .booker(user2)
            .status(Status.WAITING)
            .build();

    BookingDtoIn bookingDtoIn1 = BookingDtoIn.builder()
            .start(LocalDateTime.of(2022, 10, 15, 10, 0, 0))
            .ending(LocalDateTime.of(2022, 10, 16, 10, 0, 0))
            .itemId(1L)
            .status(Status.WAITING)
            .build();

    BookingDtoOut bookingDtoOut1 = BookingDtoOut.builder()
            .id(1L)
            .start(LocalDateTime.of(2022, 10, 15, 10, 0, 0))
            .ending(LocalDateTime.of(2022, 10, 16, 10, 0, 0))
            .item(itemDtoOut1)
            .booker(userDto2)
            .status(Status.WAITING)
            .build();

    @Test
    void testBookingService() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        Mockito.when(bookingRepository.findIntersectedBookings(Status.APPROVED, item1.getId(),
                        bookingDtoIn1.getStart(), bookingDtoIn1.getEnding()))
                .thenReturn(0);

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user2));

        assertEquals(bookingService.addBooking(bookingDtoIn1, userDto2.getId()).getId(), booking1.getId());

        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking1));

        bookingDtoOut1.setStatus(Status.APPROVED);
        assertEquals(bookingService.updateBookingStatus(user1.getId(), booking1.getId(), "true").getStatus(),
                bookingDtoOut1.getStatus());

        bookingDtoOut1.setStatus(Status.REJECTED);
        assertEquals(bookingService.updateBookingStatus(user1.getId(), booking1.getId(), "false").getStatus(),
                bookingDtoOut1.getStatus());

        assertEquals(bookingService.getBookingById(user2.getId(), booking1.getId()).getId(), bookingDtoOut1.getId());

        Mockito.when(bookingRepository.findByBookerId(Mockito.anyLong(), Mockito.any()))
                        .thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByUserId(State.ALL, user2.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

        Mockito.when(bookingRepository.findCurrentBookingsByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByUserId(State.CURRENT, user2.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

        Mockito.when(bookingRepository.findByBookerIdAndEndingBefore(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByUserId(State.PAST, user2.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

        Mockito.when(bookingRepository.findByBookerIdAndStartAfter(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByUserId(State.FUTURE, user2.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

        Mockito.when(bookingRepository.findByBookerIdAndStatusIs(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByUserId(State.WAITING, user2.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());
        assertEquals(bookingService.getBookingsByUserId(State.REJECTED, user2.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));
        Mockito.when(itemRepository.findItemByOwnerId(user1.getId(), Sort.by("id")))
                .thenReturn(List.of(item1));

        Mockito.when(bookingRepository.findByOwnerId(Mockito.anyList(), Mockito.any())).thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByOwnerId(State.ALL, user1.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

        Mockito.when(bookingRepository.findCurrentBookingsByOwnerId(Mockito.anyList(), Mockito.any()))
                .thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByOwnerId(State.CURRENT, user1.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

        Mockito.when(bookingRepository.findByItemIdInAndEndingBefore(Mockito.anyList(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByOwnerId(State.PAST, user1.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

        Mockito.when(bookingRepository.findByItemIdInAndStartAfter(Mockito.anyList(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByOwnerId(State.FUTURE, user1.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

        Mockito.when(bookingRepository.findByItemIdInAndStatusIs(Mockito.anyList(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking1));

        assertEquals(bookingService.getBookingsByOwnerId(State.WAITING, user1.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());
        assertEquals(bookingService.getBookingsByOwnerId(State.REJECTED, user1.getId(), 0, 10).get(0).getId(),
                bookingDtoOut1.getId());

    }
}