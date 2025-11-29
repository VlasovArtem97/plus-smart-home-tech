package ru.practicum.hub.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class HandlerException {

    private ApiException build(HttpStatus status, String reason, String message) {
        return ApiException.builder()
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiException handlerMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        String allError = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.joining("; "));
        log.error("Incorrectly made request (MethodArgumentNotValidException error) : {}", allError);
        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request", allError);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiException handleConstraintViolation(final ConstraintViolationException e) {
        String allError = e.getConstraintViolations().stream()
                .map(violation -> String.format("Field: %s. Error: %s",
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.joining("; "));
        log.error("Incorrectly made request (ConstraintViolationException error): {}", allError);
        return build(HttpStatus.CONFLICT, "Incorrectly made request", allError);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiException handleMissingServletRequestParameter(final MissingServletRequestParameterException e) {
        log.error("Incorrectly made request (MissingServletRequestParameterException error): {}", e.getParameterName());
        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request", e.getParameterName());
    }


    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiException handleIllegalState(final IllegalStateException e) {
        log.error("Incorrectly made request (IllegalStateException error): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiException handleException(final Exception e) {
        log.error("Internal server error - {}", e.getMessage());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.", e.getMessage());
    }
}
