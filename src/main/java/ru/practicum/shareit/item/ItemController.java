package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.error.validation.Create;
import ru.practicum.shareit.error.validation.Update;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                                           @RequestHeader("X-Sharer-User-Id") long userId) { //todo validated
        log.info("Обработка эндпойнта POST /items(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(itemService.addItemToStorage(itemDto, userId), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable long itemId,
                                              @Validated({Update.class}) @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта PATCH /items/" + itemId + "(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(itemService.updateItem(itemDto, itemId, userId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable long itemId) {
        log.info("Обработка эндпойнта GET /items/" + itemId + ".");
        return new ResponseEntity<>(itemService.getItemById(itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта GET /items(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(itemService.getItemsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam(name = "text") String text,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта GET /items/search?text=" + text + "(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(itemService.searchAvailableItemsByPartOfName(text, userId), HttpStatus.OK);
    }
}
