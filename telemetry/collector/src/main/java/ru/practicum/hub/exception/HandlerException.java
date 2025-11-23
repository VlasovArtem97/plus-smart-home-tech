package ru.practicum.hub.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class HandlerException {

    private ApiException build(HttpStatus status, String reason, String message) {
        return ApiException.builder()
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
