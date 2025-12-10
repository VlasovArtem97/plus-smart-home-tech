package ru.practicum.exception;

public class AvroDeserializer extends RuntimeException {
    public AvroDeserializer(String message, Throwable cause) {
        super(message, cause);
    }
}
