package ru.practicum.exception;

public class IncorrectDeliveryStateException extends RuntimeException {
    public IncorrectDeliveryStateException(String message) {
        super(message);
    }
}
