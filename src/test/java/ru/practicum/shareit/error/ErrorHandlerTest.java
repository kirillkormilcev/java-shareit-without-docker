package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.error.exception.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void testErrorHandler() {
        String notFound = "Объект(-ы) не найден(-ы).";
        assertEquals(Objects.requireNonNull(errorHandler
                        .handleNotFoundException(new NotFoundException(notFound)).getBody()).getMessage(),
                notFound);

        String incorrectRequestParam = "Не корректный параметр запроса.";
        assertEquals(errorHandler
                        .handleIncorrectRequestParamException(new IncorrectRequestParamException(incorrectRequestParam))
                        .getStatusCode(),
                HttpStatus.BAD_REQUEST);

        String userValidation = "Не корректно(-ы)е поле(-я) пользователя.";
        assertEquals(Objects.requireNonNull(errorHandler
                        .handleUserValidationException(new UserValidationException(userValidation)).getBody()).getMessage(),
                userValidation);

        String throwable = "Неожиданная ошибка.";
        assertEquals(errorHandler.handleThrowable(new Throwable(throwable)).getStatusCode(),
                HttpStatus.INTERNAL_SERVER_ERROR);

        String itemValidation = "Проблема с бронируемой вещью.";
        assertEquals(errorHandler
                        .handleItemValidationException(new ItemValidationException(itemValidation)).getStatusCode(),
                HttpStatus.BAD_REQUEST);

        String bookingValidation = "Проблема с бронью.";
        assertEquals(errorHandler
                        .handleBookingValidationException(new BookingValidationException(bookingValidation)).getStatusCode(),
                HttpStatus.BAD_REQUEST);

        String incorrectStatus = "Unknown state: UNSUPPORTED_STATUS";
        assertEquals(errorHandler
                        .handleIncorrectStatusException(new IncorrectStatusException(incorrectStatus)).getStatusCode(),
                HttpStatus.BAD_REQUEST);
    }
}