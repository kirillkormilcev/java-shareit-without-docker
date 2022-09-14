package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.exception.IncorrectRequestParamException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.UserValidationException;

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
}
