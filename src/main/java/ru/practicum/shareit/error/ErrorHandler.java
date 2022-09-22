package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.exception.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("Объект(-ы) не найден(-ы).", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIncorrectRequestParamException(final IncorrectRequestParamException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("Не корректный параметр запроса.", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserValidationException(final UserValidationException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("Не корректно(-ы)е поле(-я) пользователя.", e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleThrowable(final Throwable e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("Неожиданная ошибка.", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("Не корректно(-ы)е поле(-я) пользователя.", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleItemValidationException(final ItemValidationException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("Проблема с бронируемой вещью.", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBookingValidationException(final BookingValidationException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("Проблема с бронью.", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIncorrectStatusException(final IncorrectStatusException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
