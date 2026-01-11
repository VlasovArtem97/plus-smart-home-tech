package ru.practicum.contract.interactionapi.exception.fiegnclient;

import org.springframework.http.HttpStatus;

public class NotAuthorizedException extends RuntimeException {

    private final HttpStatus httpStatus;

    public NotAuthorizedException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
