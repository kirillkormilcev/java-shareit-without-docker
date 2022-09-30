package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class BookingService {
    final BookingRepository bookingRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Transactional
    public BookingDtoOut addBooking(BookingDtoIn bookingDtoIn, long userId) {
        Item item = isItemAvailable(bookingDtoIn);
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Нельзя арендовать у самого себя.");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoIn);
        BookingMapper.setItem(booking, item);
        BookingMapper.setBooker(booking, findUserById(userId));
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDtoOut updateBookingStatus(long userId, long bookingId, String approved) {
        findUserById(userId);
        Booking booking = findBookingById(bookingId);
        isUserOwner(userId, findItemById(booking.getItem().getId()));
        if (Objects.equals(approved, "true")) {
            if (booking.getStatus() == Status.APPROVED) {
                throw new IncorrectRequestParamException("Бронирование уже одобрено.");
            }
            booking.setStatus(Status.APPROVED);
        } else if (Objects.equals(approved, "false")) {
            if (booking.getStatus() == Status.REJECTED) {
                throw new IncorrectRequestParamException("Бронирование уже отклонено.");
            }
            booking.setStatus(Status.REJECTED);
        } else {
            throw new BookingValidationException("Передан не верный параметр approved = " + approved + ".\n" +
                    "Должен быть true или false.");
        }
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    public BookingDtoOut getBookingById(long userId, long bookingId) {
        findUserById(userId);
        Booking booking = findBookingById(bookingId);
        Item item = findItemById(booking.getItem().getId());
        isUserBookerOrOwner(userId, booking, item);
        return BookingMapper.toBookingDtoOut(booking);
    }

    public List<BookingDtoOut> getBookingsByUserId(State state, long userId) {
        findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result;
        Sort sort = Sort.by("start").descending();
        switch (state) {
            case ALL:
                result = bookingRepository.findByBookerId(userId, sort);
                break;
            case CURRENT:
                result = bookingRepository.findCurrentBookingsByBookerId(userId);
                break;
            case PAST:
                result = bookingRepository.findByBookerIdAndEndingBefore(userId, now, sort);
                break;
            case FUTURE:
                result = bookingRepository.findByBookerIdAndStartAfter(userId, now, sort);
                break;
            case WAITING:
                result = bookingRepository.findByBookerIdAndStatusIs(userId, Status.WAITING, sort);
                break;
            case REJECTED:
                result = bookingRepository.findByBookerIdAndStatusIs(userId, Status.REJECTED, sort);
                break;
            default:
                throw new IncorrectStatusException("При запросе пользователем его бронирований указан не верный" +
                        " параметр state = " + state + ".");
        }
        return result.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    public List<BookingDtoOut> getBookingsByOwnerId(State state, long ownerId) {
        findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIdsByOwnerId = itemRepository.findItemByOwnerId(ownerId, Sort.by("id")).stream().map(Item::getId)
                .collect(Collectors.toList());
        if (itemIdsByOwnerId.size() == 0) {
            throw new NotFoundException("Вы не владеете ни одной вещью.");
        }
        List<Booking> result;
        Sort sort = Sort.by("start").descending();
        switch (state) {
            case ALL:
                result = bookingRepository.findByOwnerId(itemIdsByOwnerId);
                break;
            case CURRENT:
                result = bookingRepository.findCurrentBookingsByOwnerId(itemIdsByOwnerId);
                break;
            case PAST:
                result = bookingRepository.findByItemIdInAndEndingBefore(itemIdsByOwnerId, now, sort);
                break;
            case FUTURE:
                result = bookingRepository.findByItemIdInAndStartAfter(itemIdsByOwnerId, now, sort);
                break;
            case WAITING:
                result = bookingRepository.findByItemIdInAndStatusIs(itemIdsByOwnerId, Status.WAITING, sort);
                break;
            case REJECTED:
                result = bookingRepository.findByItemIdInAndStatusIs(itemIdsByOwnerId, Status.REJECTED, sort);
                break;
            default:
                throw new IncorrectStatusException("При запросе собственником бронирований его вещей указан не " +
                        "верный параметр state = " + state + ".");
        }
        return result.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь не найдена в базе."));
    }

    private Booking findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Аренда с индексом " + bookingId + " не найдена в базе."));
    }

    private void isUserBookerOrOwner(long userId, Booking booking, Item item) {
        if (!(booking.getBooker().getId() == userId || item.getOwner().getId() == userId)) {
            throw new NotFoundException("Вы не создатель аренды с индексом: " + booking.getId() + ". \n" +
                    "Или вы не хозяин вещи с индексом: " + item.getId() + ".");
        }
    }

    private void isUserOwner(long userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Вы не хозяин вещи с индексом: " + item.getId() + ".");
        }
    }

    private Item isItemAvailable(BookingDtoIn bookingDtoIn) {
        long itemId = bookingDtoIn.getItemId();
        Item item = findItemById(itemId);
        if (!item.getAvailable() || bookingRepository.findIntersectedBookings(Status.APPROVED, itemId,
                bookingDtoIn.getStart(), bookingDtoIn.getEnding()) > 0) {
            throw new ItemValidationException("Вещь с индексом " + bookingDtoIn.getItemId() + " в настоящий момент не доступна.");
        }

        return item;
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с индексом " + userId + " не найден в базе."));
    }
}
