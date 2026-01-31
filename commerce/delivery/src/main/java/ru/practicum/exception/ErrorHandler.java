package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.contract.interactionapi.exception.ApiError;
import ru.practicum.contract.interactionapi.exception.GlobalErrorHandler;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends GlobalErrorHandler {

    @ExceptionHandler(IncorrectDeliveryStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectDeliveryStateException(IncorrectDeliveryStateException e) {
        log.error("Ошибка: неверный статус доставки (IncorrectOrderStateException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка: неверный статус доставки.", e.getMessage(),
                IncorrectDeliveryStateException.class.getSimpleName());
    }

    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoDeliveryFoundException(NoDeliveryFoundException e) {
        log.error("Ошибка: Доставка не найдена (NoDeliveryFoundException): {}", e.getMessage());
        return build(HttpStatus.NOT_FOUND, "Ошибка: доставка не найдена.", e.getMessage(),
                NoDeliveryFoundException.class.getSimpleName());
    }

    @ExceptionHandler(OrderExistsInDeliveryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleOrderExistsInDeliveryException(OrderExistsInDeliveryException e) {
        log.error("Ошибка: Доставка уже существует (OrderExistsInDeliveryException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка: доставка уже существует.", e.getMessage(),
                OrderExistsInDeliveryException.class.getSimpleName());
    }
}
