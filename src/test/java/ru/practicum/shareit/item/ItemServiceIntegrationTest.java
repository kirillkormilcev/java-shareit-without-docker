package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.error.exception.IncorrectRequestParamException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareitItem",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceIntegrationTest {

    private final ItemService itemService;

    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

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

    Request request1 = Request.builder()
            .id(1L)
            .description("r_desc")
            .requestor(user1)
            .build();

    @Test
    void testItemService() {
        itemService.getItemRepository().save(item1);
        itemService.getRequestRepository().save(request1);

        long wrongUserId = 100L;
        NotFoundException ex1 = assertThrows(NotFoundException.class, () ->
                itemService.addItem(itemDtoIn1, wrongUserId));

        assertEquals("Пользователь с индексом " + wrongUserId + " не найден в базе.", ex1.getMessage());

        IncorrectRequestParamException ex2 = assertThrows(IncorrectRequestParamException.class, () ->
                itemService.updateItem(null, item1.getId(), user1.getId()));

        assertEquals("На обновление поступила null вещь.", ex2.getMessage());

        NotFoundException ex3 = assertThrows(NotFoundException.class, () ->
                itemService.updateItem(itemDtoIn1, item1.getId(), wrongUserId));

        assertEquals("Редактировать вещь имеет право только хозяин.", ex3.getMessage());

        Mockito.when(bookingRepository.findEndBookingOfItemByUser(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(null);

        IncorrectRequestParamException ex4 = assertThrows(IncorrectRequestParamException.class, () ->
                itemService.addComment(user1.getId(), item1.getId(), commentDto1));

        assertEquals("Вы не арендовали вещь с индексом " + item1.getId() + " или аренда еще не закончена.",
                ex4.getMessage());

        long wrongItemId = 100L;
        NotFoundException ex5 = assertThrows(NotFoundException.class, () ->
                itemService.updateItem(itemDtoIn1, wrongItemId, user1.getId()));

        assertEquals("Вещь с индексом " + wrongItemId + " не найдена в базе.", ex5.getMessage());

        long wrongRequestId = 100L;
        itemDtoIn1.setRequestId(wrongRequestId);

        NotFoundException ex6 = assertThrows(NotFoundException.class, () ->
                itemService.addItem(itemDtoIn1, user1.getId()));

        assertEquals("Запрос с индексом " + wrongRequestId + " не найден в базе.", ex6.getMessage());
    }
}