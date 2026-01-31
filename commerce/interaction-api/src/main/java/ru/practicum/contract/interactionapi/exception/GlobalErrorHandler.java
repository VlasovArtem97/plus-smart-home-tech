package ru.practicum.contract.interactionapi.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.contract.interactionapi.exception.commerce.IncorrectOrderStateException;
import ru.practicum.contract.interactionapi.exception.commerce.NoOrderFoundException;
import ru.practicum.contract.interactionapi.exception.commerce.NotAuthorizedUserException;
import ru.practicum.contract.interactionapi.exception.commerce.NotEnoughInfoInOrderToCalculateException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.BadRequestException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.InternalServerErrorException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotAuthorizedException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
public abstract class GlobalErrorHandler {
    protected ApiError build(HttpStatus status, String reason, String message, String errorType) {
        return ApiError.builder()
                .message(message)
                .reason(reason)
                .status(status.name())
                .errorType(errorType)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        String allError = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.joining("; "));
        log.error("Incorrectly made request (MethodArgumentNotValidException error) : {}", allError);
        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request", allError,
                MethodArgumentNotValidException.class.getSimpleName());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolation(final ConstraintViolationException e) {
        String allError = e.getConstraintViolations().stream()
                .map(violation -> String.format("Field: %s. Error: %s",
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.joining("; "));
        log.error("Incorrectly made request (ConstraintViolationException error): {}", allError);
        return build(HttpStatus.CONFLICT, "Incorrectly made request", allError,
                ConstraintViolationException.class.getSimpleName());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameter(final MissingServletRequestParameterException e) {
        log.error("Incorrectly made request (MissingServletRequestParameterException error): {}", e.getParameterName());
        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request", e.getParameterName(),
                MissingServletRequestParameterException.class.getSimpleName());
    }


    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalState(final IllegalStateException e) {
        log.error("Incorrectly made request (IllegalStateException error): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Incorrectly made request", e.getMessage(),
                IllegalStateException.class.getSimpleName());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle409(Exception e) {
        log.error("Integrity constraint has been violated (Exception error): {}", e.getMessage());
        return build(HttpStatus.CONFLICT, "Integrity constraint has been violated.", e.getMessage(),
                DataIntegrityViolationException.class.getSimpleName());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.error("Internal server error - {}", e.getMessage());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.", e.getMessage(),
                Exception.class.getSimpleName());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleFeignClientBadRequestException(final BadRequestException e) {
        log.error("Ошибка в обращении через feignClient (BadRequestException): {}", e.getMessage());
        return build(e.getHttpStatus(), "Ошибка в обращении через feignClient", e.getMessage(),
                BadRequestException.class.getSimpleName());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleFeignClientNotFoundException(final NotFoundException e) {
        log.error("Ошибка в обращении через feignClient (NotFoundException): {}", e.getMessage());
        return build(e.getHttpStatus(), "Ошибка в обращении через feignClient", e.getMessage(),
                NotFoundException.class.getSimpleName());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleFeignClientInternalServerErrorException(final InternalServerErrorException e) {
        log.error("Ошибка в обращении через feignClient (InternalServerErrorException): {}", e.getMessage());
        return build(e.getHttpStatus(), "Ошибка в обращении через feignClient", e.getMessage(),
                InternalServerErrorException.class.getSimpleName());
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleFeignClientNotFoundException(final NotAuthorizedException e) {
        log.error("Ошибка в обращении через feignClient (NotAuthorizedException): {}", e.getMessage());
        return build(e.getHttpStatus(), "Ошибка в обращении через feignClient", e.getMessage(),
                NotAuthorizedException.class.getSimpleName());
    }

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNotEnoughInfoInOrderToCalculateException(NotEnoughInfoInOrderToCalculateException e) {
        log.error("Ошибка: недостаточно информации (NotEnoughInfoInOrderToCalculateException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка: недостаточно информации.", e.getMessage(),
                NotEnoughInfoInOrderToCalculateException.class.getSimpleName());
    }

    @ExceptionHandler(NoOrderFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoOrderFoundException(NoOrderFoundException e) {
        log.error("Ошибка: заказ не найден (NoOrderFoundException): {}", e.getMessage());
        return build(HttpStatus.NOT_FOUND, "Ошибка: заказ не найден.", e.getMessage(),
                NoOrderFoundException.class.getSimpleName());
    }

    @ExceptionHandler(IncorrectOrderStateException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleIncorrectOrderStateException(IncorrectOrderStateException e) {
        log.error("Ошибка: неверный статус заказа (IncorrectOrderStateException): {}", e.getMessage());
        return build(HttpStatus.NOT_FOUND, "Ошибка: неверный статус заказа.", e.getMessage(),
                IncorrectOrderStateException.class.getSimpleName());
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleNotAuthorizedUserException(NotAuthorizedUserException e) {
        log.error("Ошибка: имя пользователя не должно быть пустым (SNotAuthorizedUserException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка, имя пользователя не должно быть пустым.", e.getMessage(),
                NotAuthorizedUserException.class.getSimpleName());
    }

}
