package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.contract.interactionapi.exception.ApiError;
import ru.practicum.contract.interactionapi.exception.GlobalErrorHandler;

@Slf4j
@RestControllerAdvice
public class ErrorHandler extends GlobalErrorHandler {

    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNotFoundUserException(NotFoundUserException e) {
        log.error("Ошибка: пользователь не найден (NotFoundUserException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка: пользователь не найден.", e.getMessage(),
                NotFoundUserException.class.getSimpleName());
    }
}
