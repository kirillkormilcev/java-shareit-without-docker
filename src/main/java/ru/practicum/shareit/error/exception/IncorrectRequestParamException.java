package ru.practicum.shareit.error.exception;

public class IncorrectRequestParamException extends RuntimeException {
    public IncorrectRequestParamException(final String message) {
        super(message);
    }
}
