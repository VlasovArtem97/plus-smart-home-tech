package ru.practicum.contract.interactionapi.exception.commerce;

public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException(String message) {
        super(message);
    }
}
