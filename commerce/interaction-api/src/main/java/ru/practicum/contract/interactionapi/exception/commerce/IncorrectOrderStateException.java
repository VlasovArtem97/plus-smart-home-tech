package ru.practicum.contract.interactionapi.exception.commerce;

public class IncorrectOrderStateException extends RuntimeException {
    public IncorrectOrderStateException(String message) {
        super(message);
    }
}
