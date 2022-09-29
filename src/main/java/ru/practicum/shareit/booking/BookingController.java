package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.error.validation.Create;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {
    final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoOut> addBooking(@Validated({Create.class}) @RequestBody BookingDtoIn bookingDtoIn,
                                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта POST /bookings(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(bookingService.addBooking(bookingDtoIn, userId), HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> updateBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @PathVariable long bookingId,
                                                             @RequestParam(name = "approved") String approved) {
        log.info("Обработка эндпойнта PATCH /bookings/{bookingId=" + bookingId + "}?approved=" + approved
                + "(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(bookingService.updateBookingStatus(userId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOut> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable long bookingId) {
        log.info("Обработка эндпойнта GET /bookings/{bookingId=" + bookingId + "(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(bookingService.getBookingById(userId, bookingId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoOut>> getAllBookingOfUser(@RequestParam(name = "state", required = false,
            defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта GET /bookings/(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(bookingService.getBookingsByUserId(State.stringToEnum(state.toUpperCase()),
                userId), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoOut>> getAllBookingOfOwner(@RequestParam(name = "state", required = false,
            defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Обработка эндпойнта GET /bookings/(X-Sharer-User-Id=" + ownerId + ").");
        return new ResponseEntity<>(bookingService.getBookingsByOwnerId(State.stringToEnum(state.toUpperCase()),
                ownerId), HttpStatus.OK);
    }
}
