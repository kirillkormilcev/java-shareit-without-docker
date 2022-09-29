package ru.practicum.shareit.error.exception;

public class BookingValidationException extends RuntimeException {

    public BookingValidationException(final String message) {
        super(message);
    }
}
