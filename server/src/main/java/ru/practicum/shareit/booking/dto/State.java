package ru.practicum.shareit.booking.dto;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State parseState(String stateString) {
        try {
            return State.valueOf(stateString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ru.practicum.shareit.exception.ValidationException("Неизвестный state: " + stateString);
        }
    }
}