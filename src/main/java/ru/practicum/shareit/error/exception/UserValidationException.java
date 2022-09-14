package ru.practicum.shareit.error.exception;

public class UserValidationException extends RuntimeException {

    public UserValidationException(final String message) {
        super(message);
    }
}
