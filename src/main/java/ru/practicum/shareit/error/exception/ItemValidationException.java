package ru.practicum.shareit.error.exception;

public class ItemValidationException extends RuntimeException {

    public ItemValidationException(final String message) {
        super(message);
    }
}
