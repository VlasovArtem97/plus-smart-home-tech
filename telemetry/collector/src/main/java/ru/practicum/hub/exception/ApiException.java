package ru.practicum.hub.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiException {

    private final String message;
    private final String reason;
    private final String status;
    private final LocalDateTime timestamp;
}
