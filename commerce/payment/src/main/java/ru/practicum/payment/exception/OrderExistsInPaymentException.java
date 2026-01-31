package ru.practicum.payment.exception;

public class OrderExistsInPaymentException extends RuntimeException {
    public OrderExistsInPaymentException(String message) {
        super(message);
    }
}
