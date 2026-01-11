package ru.practicum.contract.interactionapi.exception.fiegnclient;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
