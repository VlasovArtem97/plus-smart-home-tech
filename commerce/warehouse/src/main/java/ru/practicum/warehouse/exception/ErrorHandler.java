package ru.practicum.warehouse.exception;

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

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleSpecifiedProductAlreadyInWarehouseException(SpecifiedProductAlreadyInWarehouseException e) {
        log.error("Ошибка: товар с таким описанием уже зарегистрирован на складе " +
                "(SpecifiedProductAlreadyInWarehouseException): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка, товар с таким описанием уже зарегистрирован на складе.",
                e.getMessage(), SpecifiedProductAlreadyInWarehouseException.class.getSimpleName());
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleProductInShoppingCartLowQuantityInWarehouse(ProductInShoppingCartLowQuantityInWarehouse e) {
        log.error("Ошибка: товар из корзины не находится в требуемом количестве на складе " +
                "(ProductInShoppingCartLowQuantityInWarehouse): {}", e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Ошибка, товар из корзины не находится в требуемом количестве " +
                "на складе.", e.getMessage(), ProductInShoppingCartLowQuantityInWarehouse.class.getSimpleName());
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException e) {
        log.error("Ошибка: нет информации о товаре на складе (NoSpecifiedProductInWarehouseException): {}",
                e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Нет информации о товаре на складе.", e.getMessage(),
                NoSpecifiedProductInWarehouseException.class.getSimpleName());
    }

    @ExceptionHandler(NoSpecifiedOrderInOrderBookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoSpecifiedOrderInOrderBookingException(NoSpecifiedOrderInOrderBookingException e) {
        log.error("Ошибка: нет информации о забронированных товаров на складе (NoSpecifiedOrderInOrderBookingException): {}",
                e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Нет информации о забронированных товаров на складе.", e.getMessage(),
                NoSpecifiedOrderInOrderBookingException.class.getSimpleName());
    }

}
