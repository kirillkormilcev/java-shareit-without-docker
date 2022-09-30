package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.PageRequestModified;
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
import java.util.ArrayList;
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

    public List<BookingDtoOut> getBookingsByUserId(State state, long userId, Integer from, Integer size) {
        findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = new ArrayList<>();
        final PageRequest pageRequest = new PageRequestModified(from, size, Sort.by("start").descending());
        switch (state) {
            case ALL:
                result = bookingRepository.findByBookerId(userId, pageRequest);
                break;
            case CURRENT:
                result = bookingRepository.findCurrentBookingsByBookerId(userId, pageRequest);
                break;
            case PAST:
                result = bookingRepository.findByBookerIdAndEndingBefore(userId, now, pageRequest);
                break;
            case FUTURE:
                result = bookingRepository.findByBookerIdAndStartAfter(userId, now, pageRequest);
                break;
            case WAITING:
                result = bookingRepository.findByBookerIdAndStatusIs(userId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                result = bookingRepository.findByBookerIdAndStatusIs(userId, Status.REJECTED, pageRequest);
                break;
        }
        return result.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    public List<BookingDtoOut> getBookingsByOwnerId(State state, long ownerId, Integer from, Integer size) {
        findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIdsByOwnerId = itemRepository.findItemByOwnerId(ownerId, Sort.by("id")).stream().map(Item::getId)
                .collect(Collectors.toList());
        if (itemIdsByOwnerId.size() == 0) {
            throw new NotFoundException("Вы не владеете ни одной вещью.");
        }
        List<Booking> result = new ArrayList<>();
        final PageRequest pageRequest = new PageRequestModified(from, size, Sort.by("start").descending());
        switch (state) {
            case ALL:
                result = bookingRepository.findByOwnerId(itemIdsByOwnerId, pageRequest);
                break;
            case CURRENT:
                result = bookingRepository.findCurrentBookingsByOwnerId(itemIdsByOwnerId, pageRequest);
                break;
            case PAST:
                result = bookingRepository.findByItemIdInAndEndingBefore(itemIdsByOwnerId, now, pageRequest);
                break;
            case FUTURE:
                result = bookingRepository.findByItemIdInAndStartAfter(itemIdsByOwnerId, now, pageRequest);
                break;
            case WAITING:
                result = bookingRepository.findByItemIdInAndStatusIs(itemIdsByOwnerId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                result = bookingRepository.findByItemIdInAndStatusIs(itemIdsByOwnerId, Status.REJECTED, pageRequest);
                break;
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

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }
}
