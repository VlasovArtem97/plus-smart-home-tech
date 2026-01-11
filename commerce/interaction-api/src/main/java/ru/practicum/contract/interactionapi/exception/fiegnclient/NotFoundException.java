package ru.practicum.contract.interactionapi.exception.fiegnclient;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
