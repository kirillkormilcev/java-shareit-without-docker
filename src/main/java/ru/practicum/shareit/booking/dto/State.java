package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.error.exception.IncorrectStatusException;

import java.util.Objects;

public enum State {
    WAITING,
    REJECTED,
    ALL,
    CURRENT,
    PAST,
    FUTURE;

    public static State stringToEnum(String state) {
        for (State stateEnum: State.values()) {
            if (Objects.equals(stateEnum.toString(), state)) {
                return State.valueOf(state);
            }
        }
        throw new IncorrectStatusException("При запросе собственником бронирований его вещей указан не " +
                "верный параметр state = " + state + ".");
    }
}
