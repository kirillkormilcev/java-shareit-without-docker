package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.common.PageRequestModified;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.exception.IncorrectRequestParamException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;
    final RequestRepository requestRepository;

    @Transactional
    public ItemDtoOut addItem(ItemDtoIn itemDtoIn, long userId) {
        Item item = ItemMapper.toItem(itemDtoIn);
        if (itemDtoIn.getRequestId() != null) {
            setRequestToItem(item, itemDtoIn);
        }
        ItemMapper.setOwner(item, findUserById(userId));
        return ItemMapper.toItemDtoOut(itemRepository.save(item));
    }

    @Transactional
    public ItemDtoOut updateItem(ItemDtoIn itemDtoIn, long itemId, long userId) {
        if (itemDtoIn == null) {
            throw new IncorrectRequestParamException("???? ???????????????????? ?????????????????? null ????????.");
        }
        Item itemFromRepo = findById(itemId);
        if (userId != itemFromRepo.getOwner().getId()) {
            throw new NotFoundException("?????????????????????????? ???????? ?????????? ?????????? ???????????? ????????????.");
        }
        ItemMapper.updateNotNullField(ItemMapper.toItem(itemDtoIn), itemFromRepo);
        if (itemDtoIn.getRequestId() != null) {
            setRequestToItem(itemFromRepo, itemDtoIn);
        }
        return ItemMapper.toItemDtoOut(itemRepository.save(itemFromRepo));
    }

    public ItemDtoOut getItemById(long itemId, long userId) {
        Item itemFromRepo = findById(itemId);
        ItemDtoOut itemDtoOut;
        if (userId == itemFromRepo.getOwner().getId()) {
            itemDtoOut = ItemMapper.toItemDtoOut(itemFromRepo);
            LocalDateTime now = LocalDateTime.now();
            findLastBookingOfItem(itemDtoOut.getId(), now).ifPresent(booking ->
                    ItemMapper.setLastBooking(itemDtoOut, BookingMapper.toBookingDtoOutForItem(booking)));
            findNextBookingOfItem(itemDtoOut.getId(), now).ifPresent(booking ->
                    ItemMapper.setNextBooking(itemDtoOut, BookingMapper.toBookingDtoOutForItem(booking)));
        } else {
            itemDtoOut = ItemMapper.toItemDtoOut(itemFromRepo);
        }
        ItemMapper.setComments(itemDtoOut, findCommentsByItemId(itemId));
        return itemDtoOut;
    }

    public List<ItemDtoOut> getItemsByUserId(long userId, Integer from, Integer size) {
        List<Item> itemsByUserId = itemRepository.findItemByOwnerId(userId,
                new PageRequestModified(from, size, Sort.by("id")));
        LocalDateTime now = LocalDateTime.now();
        return itemsByUserId.stream().map((item) -> {
            ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
            findLastBookingOfItem(itemDtoOut.getId(), now).ifPresent(booking ->
                    ItemMapper.setLastBooking(itemDtoOut, BookingMapper.toBookingDtoOutForItem(booking)));
            findNextBookingOfItem(itemDtoOut.getId(), now).ifPresent(booking ->
                    ItemMapper.setNextBooking(itemDtoOut, BookingMapper.toBookingDtoOutForItem(booking)));
            ItemMapper.setComments(itemDtoOut, findCommentsByItemId(itemDtoOut.getId()));
            return itemDtoOut;
        }).collect(Collectors.toList());
    }

    public List<ItemDtoOut> searchAvailableItemsByPartOfNameOrDescription(String text, long userId, Integer from, Integer size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemByText(text, new PageRequestModified(from, size, Sort.by("id"))).stream()
                .map(ItemMapper::toItemDtoOut).collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Item item = findById(itemId);
        if (bookingRepository.findEndBookingOfItemByUser(userId, itemId).isEmpty()) {
            throw new IncorrectRequestParamException("???? ???? ???????????????????? ???????? ?? ???????????????? " + itemId + " ?????? ???????????? ?????? ???? ??????????????????.");
        }
        Comment comment = CommentMapper.toComment(commentDto);
        CommentMapper.setItem(comment, item);
        CommentMapper.setAuthor(comment, findUserById(userId));
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private List<CommentDto> findCommentsByItemId(long itemId) {
        return commentRepository.findByItemId(itemId).stream().map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("???????????????????????? ?? ???????????????? " + userId + " ???? ???????????? ?? ????????."));
    }

    private Item findById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("???????? ?? ???????????????? " + itemId + " ???? ?????????????? ?? ????????."));
    }

    private Optional<Booking> findLastBookingOfItem(long itemId, LocalDateTime now) {
        return bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(itemId, now, Sort.by("start"), Status.APPROVED);
    }

    private Optional<Booking> findNextBookingOfItem(long itemId, LocalDateTime now) {
        return bookingRepository.findFirstNextBooking(itemId, now);
    }

    private void setRequestToItem(Item item, ItemDtoIn itemDtoIn) {
        item.setRequest(requestRepository.findById(itemDtoIn.getRequestId()).orElseThrow(() ->
                new NotFoundException("???????????? ?? ???????????????? " + itemDtoIn.getRequestId() + " ???? ???????????? ?? ????????.")));
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public BookingRepository getBookingRepository() {
        return bookingRepository;
    }

    public CommentRepository getCommentRepository() {
        return commentRepository;
    }

    public RequestRepository getRequestRepository() {
        return requestRepository;
    }
}
