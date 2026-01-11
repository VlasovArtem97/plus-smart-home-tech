package ru.practicum.contract.interactionapi.exception.fiegnclient;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String message) {
        super(message);
    }
}
