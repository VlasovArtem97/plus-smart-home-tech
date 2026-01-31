package ru.practicum.warehouse.exception;

public class NoSpecifiedOrderInOrderBookingException extends RuntimeException {
    public NoSpecifiedOrderInOrderBookingException(String message) {
        super(message);
    }
}
