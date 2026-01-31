package ru.practicum.contract.interactionapi.exception.commerce;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException(String message) {
        super(message);
    }
}
