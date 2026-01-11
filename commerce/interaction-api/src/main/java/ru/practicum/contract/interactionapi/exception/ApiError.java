package ru.practicum.contract.interactionapi.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiError {
    private final String message;
    private final String reason;
    private final String status;
    private final String errorType;
    private final LocalDateTime timestamp;
}
