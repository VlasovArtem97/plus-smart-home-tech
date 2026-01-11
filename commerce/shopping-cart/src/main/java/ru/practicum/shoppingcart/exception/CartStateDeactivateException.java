package ru.practicum.shoppingcart.exception;

public class CartStateDeactivateException extends RuntimeException {
    public CartStateDeactivateException(String message) {
        super(message);
    }
}
