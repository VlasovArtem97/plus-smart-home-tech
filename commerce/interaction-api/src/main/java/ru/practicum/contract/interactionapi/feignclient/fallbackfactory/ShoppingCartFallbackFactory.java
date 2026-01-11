package ru.practicum.contract.interactionapi.feignclient.fallbackfactory;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.exception.fiegnclient.BadRequestException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.InternalServerErrorException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotAuthorizedException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotFoundException;
import ru.practicum.contract.interactionapi.feignclient.ShoppingCartClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ShoppingCartFallbackFactory implements FallbackFactory<ShoppingCartClient> {

    @Override
    public ShoppingCartClient create(Throwable cause) {
        return new ShoppingCartClient() {
            @Override
            public ShoppingCartDto getShoppingCart(String userName) {
                throw handleException(cause);
            }

            @Override
            public ShoppingCartDto addProductToShoppingCart(Map<UUID, Long> products, String userName) {
                throw handleException(cause);
            }

            @Override
            public void deactivateCurrentShoppingCart(String userName) {
                throw handleException(cause);
            }

            @Override
            public ShoppingCartDto removeFromShoppingCart(String userName, List<UUID> productId) {
                throw handleException(cause);
            }

            @Override
            public ShoppingCartDto changeProductQuantity(String userName, ChangeProductQuantityRequest quantityRequest) {
                throw handleException(cause);
            }
        };
    }

    private RuntimeException handleException(Throwable cause) {
        return switch (cause) {
            case NotFoundException notFoundException -> new NotFoundException(cause.getMessage(), HttpStatus.NOT_FOUND);
            case BadRequestException badRequestException -> new BadRequestException(cause.getMessage(),
                    HttpStatus.BAD_REQUEST);
            case NotAuthorizedException notAuthorizedException -> new NotAuthorizedException(cause.getMessage(),
                    HttpStatus.UNAUTHORIZED);
            case InternalServerErrorException internalServerErrorException ->
                    new InternalServerErrorException(cause.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            case null, default -> new RuntimeException("Произошла ошибка при вызове ShoppingCart service", cause);
        };
    }
}
