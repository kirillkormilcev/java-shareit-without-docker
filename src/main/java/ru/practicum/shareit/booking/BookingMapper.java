package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoOutForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDtoOut toBookingDtoOut(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .ending(booking.getEnding())
                .item(booking.getItem() != null ? ItemMapper.toItemDtoOut(booking.getItem()) : null)
                .status(booking.getStatus())
                .booker(booking.getBooker() != null ? UserMapper.toUserDto(booking.getBooker()) : null)
                .build();
    }

    public static BookingDtoOutForItem toBookingDtoOutForItem(Booking booking) {
        return BookingDtoOutForItem.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .build();
    }

    public static Booking toBooking(BookingDtoIn bookingDtoIn) {
        return Booking.builder()
                .start(bookingDtoIn.getStart())
                .ending(bookingDtoIn.getEnding())
                .status(bookingDtoIn.getStatus())
                .build();
    }

    public static void setItem(Booking booking, Item item) {
        booking.setItem(item);
    }

    public static void setBooker(Booking booking, User booker) {
        booking.setBooker(booker);
    }
}
