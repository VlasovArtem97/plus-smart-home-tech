package ru.practicum.payment.exception;

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

    @ExceptionHandler(IncorrectPaymentStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectPaymentStateException(IncorrectPaymentStateException e) {
        log.error("Ошибка: неверный статус доставки (IncorrectPaymentStateException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка: неверный статус доставки.", e.getMessage(),
                IncorrectPaymentStateException.class.getSimpleName());
    }

    @ExceptionHandler(NoPaymentFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoPaymentFoundException(NoPaymentFoundException e) {
        log.error("Ошибка: платеж не найден (NoPaymentFoundException): {}", e.getMessage());
        return build(HttpStatus.NOT_FOUND, "Ошибка: платеж не найден.", e.getMessage(),
                NoPaymentFoundException.class.getSimpleName());
    }

    @ExceptionHandler(OrderExistsInPaymentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleOrderExistsInPaymentException(OrderExistsInPaymentException e) {
        log.error("Ошибка: платеж уже существует (OrderExistsInPaymentException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка: платеж уже существует.", e.getMessage(),
                OrderExistsInPaymentException.class.getSimpleName());
    }
}
