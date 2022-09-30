package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.error.validation.Create;
import ru.practicum.shareit.error.validation.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDtoOut> addItem(@Validated({Create.class}) @RequestBody ItemDtoIn itemDtoIn,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта POST /items(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(itemService.addItem(itemDtoIn, userId), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDtoOut> updateItem(@PathVariable long itemId,
                                                 @Validated({Update.class}) @RequestBody ItemDtoIn itemDtoIn,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта PATCH /items/" + itemId + "(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(itemService.updateItem(itemDtoIn, itemId, userId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoOut> getItem(@PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта GET /items/(X-Sharer-User-Id=" + userId + ")" + itemId + ".");
        return new ResponseEntity<>(itemService.getItemById(itemId, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoOut>> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PositiveOrZero @RequestParam(name = "from", required = false,
                                                             defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", required = false,
                                                             defaultValue = "10") Integer size) {
        log.info("Обработка эндпойнта GET /items(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(itemService.getItemsByUserId(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDtoOut>> searchItems(@RequestParam(name = "text") String text,
                                                        @RequestHeader("X-Sharer-User-Id") long userId,
                                                        @PositiveOrZero @RequestParam(name = "from", required = false,
                                                                defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", required = false,
                                                                defaultValue = "10") Integer size) {
        log.info("Обработка эндпойнта GET /items/search?text=" + text + "(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(itemService.searchAvailableItemsByPartOfNameOrDescription(text, userId, from, size), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                                 @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("Обработка эндпойнта POST /items/{itemId=" + itemId + "}/comment(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(itemService.addComment(userId, itemId, commentDto), HttpStatus.OK);
    }
}
