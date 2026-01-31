package ru.practicum.shoppingstore.exception;

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

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleProductNotFoundException(ProductNotFoundException e) {
        log.error("The required object was not found (ProductNotFoundException error): {}", e.getMessage());
        return build(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage(),
                ProductNotFoundException.class.getSimpleName());
    }

}
