package ru.practicum.shoppingcart.exception;

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

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoProductsInShoppingCartException(NoProductsInShoppingCartException e) {
        log.error("Ошибка: Нет искомых товаров в корзине (NoProductsInShoppingCartException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка, Нет искомых товаров в корзине.", e.getMessage(),
                NoProductsInShoppingCartException.class.getSimpleName());
    }

    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundUserException(NotFoundUserException e) {
        log.error("Ошибка: у пользователя отсутствует корзина (NotFoundUserException): {}", e.getMessage());
        return build(HttpStatus.NOT_FOUND, "Ошибка: у пользователя отсутствует корзина.", e.getMessage(),
                NotFoundUserException.class.getSimpleName());
    }

    @ExceptionHandler(CartStateDeactivateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleCartStateDeactivateException(CartStateDeactivateException e) {
        log.error("Ошибка: Нет искомых товаров в корзине (CartStateDeactivateException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка: Корзина недоступна.", e.getMessage(),
                CartStateDeactivateException.class.getSimpleName());
    }
}
