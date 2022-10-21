package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void testItemRepository() {
        User user1 = userRepository.save(User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.ru")
                .build());

        Item item1 = itemRepository.save(Item.builder()
                .id(1L)
                .name("item1")
                .description("i_desc1")
                .available(true)
                .owner(user1)
                .build());

        Item item2 = itemRepository.save(Item.builder()
                .id(2L)
                .name("item2")
                .description("i_desc2")
                .available(true)
                .owner(user1)
                .build());

        Item item3 = itemRepository.save(Item.builder()
                .id(3L)
                .name("item3")
                .description("i_desc3")
                .available(true)
                .owner(user1)
                .build());

        Booking booking1 = bookingRepository.save(Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1L))
                .ending(LocalDateTime.now().plusDays(1L))
                .item(item1)
                .booker(user1)
                .status(Status.WAITING)
                .build());

        Booking booking2 = bookingRepository.save(Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1L))
                .ending(LocalDateTime.now().plusDays(2L))
                .item(item2)
                .booker(user1)
                .status(Status.APPROVED)
                .build());

        Booking booking3 = bookingRepository.save(Booking.builder()
                .id(3L)
                .start(LocalDateTime.now().minusDays(2L))
                .ending(LocalDateTime.now().minusDays(1L))
                .item(item3)
                .booker(user1)
                .status(Status.APPROVED)
                .build());

        Booking booking4 = bookingRepository.save(Booking.builder()
                .id(3L)
                .start(LocalDateTime.now().minusDays(2L))
                .ending(LocalDateTime.now())
                .item(item3)
                .booker(user1)
                .status(Status.APPROVED)
                .build());

        final List<Booking> findCurrentBookingsByBookerId = bookingRepository.findCurrentBookingsByBookerId(user1.getId(),
                PageRequest.of(0, 10));

        assertEquals(booking1.getId(), findCurrentBookingsByBookerId.get(0).getId());

        final List<Booking> findByOwnerId = bookingRepository.findByOwnerId(List.of(item1.getId()),
                PageRequest.of(0, 10));

        assertEquals(booking1.getId(), findByOwnerId.get(0).getId());

        final List<Booking> findCurrentBookingsByOwnerId = bookingRepository.findCurrentBookingsByOwnerId(List.of(item1.getId()),
                PageRequest.of(0, 10));

        assertEquals(booking1.getId(), findCurrentBookingsByOwnerId.get(0).getId());

        final Booking findFirstNextBooking = bookingRepository.findFirstNextBooking(item2.getId(), LocalDateTime.now())
                .orElseThrow();

        assertEquals(booking2.getId(), findFirstNextBooking.getId());

        final Booking findEndBookingOfItemByUser = bookingRepository.findEndBookingOfItemByUser(user1.getId(), item3.getId())
                .orElseThrow();

        assertEquals(booking3.getId(), findEndBookingOfItemByUser.getId());

        final Integer findIntersectedBookings = bookingRepository.findIntersectedBookings(Status.APPROVED, item3.getId(),
                        LocalDateTime.now().minusDays(1L), LocalDateTime.now());

        assertEquals(1, findIntersectedBookings);
    }

}