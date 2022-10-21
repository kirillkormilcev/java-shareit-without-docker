package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.common.PageRequestModified;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ItemServiceTest {

    ItemRepository itemRepository = mock(ItemRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    BookingRepository bookingRepository = mock(BookingRepository.class);
    CommentRepository commentRepository = mock(CommentRepository.class);
    RequestRepository requestRepository = mock(RequestRepository.class);

    ItemService itemService = new ItemService(itemRepository, userRepository, bookingRepository, commentRepository, requestRepository);

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

    Comment comment1 = Comment.builder()
            .id(1L)
            .text("comment1")
            .item(item1)
            .author(user1)
            .created(LocalDateTime.now())
            .build();

    CommentDto commentDto1 = CommentDto.builder()
            .id(1L)
            .text("comment1")
            .item(itemDtoOut1)
            .authorName(user1.getName())
            .created(LocalDateTime.now())
            .build();

    Booking booking1 = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2022, 10, 15, 10, 0, 0))
            .ending(LocalDateTime.of(2022, 10, 16, 10, 0, 0))
            .item(item1)
            .booker(user1)
            .status(Status.WAITING)
            .build();

    @Test
    void testItemService() {
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item1);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));

        assertEquals(itemService.addItem(itemDtoIn1, user1.getId()).getId(), itemDtoOut1.getId());

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item1));

        assertEquals(itemService.updateItem(itemDtoIn1, item1.getId(), user1.getId()).getId(), itemDtoOut1.getId());

        assertEquals(itemService.getItemById(itemDtoIn1.getId(), user1.getId()).getId(), itemDtoOut1.getId());

        Mockito.when(itemRepository.findItemByOwnerId(user1.getId(), new PageRequestModified(0, 10, Sort.by("id"))))
                .thenReturn(List.of(item1));

        assertEquals(itemService.getItemsByUserId(user1.getId(), 0, 10).get(0).getId(), itemDtoOut1.getId());

        Mockito.when(itemRepository.searchItemByText(item1.getDescription(), new PageRequestModified(0, 10, Sort.by("id"))))
                .thenReturn(List.of(item1));

        assertEquals(itemService.searchAvailableItemsByPartOfNameOrDescription(item1.getDescription(), user1.getId(),
                0, 10).get(0).getId(), itemDtoOut1.getId());
        assertEquals(itemService.searchAvailableItemsByPartOfNameOrDescription("", user1.getId(),
                0, 10).size(), 0);

        Mockito.when(bookingRepository.findEndBookingOfItemByUser(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment1);

        assertEquals(itemService.addComment(user1.getId(), item1.getId(), commentDto1).getId(), commentDto1.getId());
    }
}