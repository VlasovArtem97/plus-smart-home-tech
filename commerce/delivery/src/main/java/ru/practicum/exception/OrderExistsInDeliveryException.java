package ru.practicum.exception;

public class OrderExistsInDeliveryException extends RuntimeException {
    public OrderExistsInDeliveryException(String message) {
        super(message);
    }
}
