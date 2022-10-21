package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.validation.Create;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDtoOut> addRequest(@Validated({Create.class}) @RequestBody RequestDtoIn requestDtoIn,
                                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта POST /requests(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(requestService.addRequest(requestDtoIn, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RequestDtoOut>> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обработка эндпойнта GET /requests(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(requestService.getRequestsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDtoOut>> getRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PositiveOrZero @RequestParam(name = "from", required = false,
                                                                   defaultValue = "0") Integer from,
                                                           @Positive @RequestParam(name = "size", required = false,
                                                                   defaultValue = "10") Integer size) {
        log.info("Обработка эндпойнта GET /requests/all?from=" + from + "&size=" + size + "(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(requestService.getRequestsOfOtherUsers(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDtoOut> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable long requestId) {
        log.info("Обработка эндпойнта GET /requests/" + requestId + "(X-Sharer-User-Id=" + userId + ").");
        return new ResponseEntity<>(requestService.getRequestById(userId, requestId), HttpStatus.OK);
    }
}
