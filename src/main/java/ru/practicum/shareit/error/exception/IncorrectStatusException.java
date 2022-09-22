package ru.practicum.shareit.error.exception;

public class IncorrectStatusException extends RuntimeException {
    public IncorrectStatusException(final String message) {
        super(message);
    }
}
