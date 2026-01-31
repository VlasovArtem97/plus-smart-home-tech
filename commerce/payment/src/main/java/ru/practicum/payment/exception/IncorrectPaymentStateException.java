package ru.practicum.payment.exception;

public class IncorrectPaymentStateException extends RuntimeException {
    public IncorrectPaymentStateException(String message) {
        super(message);
    }
}
