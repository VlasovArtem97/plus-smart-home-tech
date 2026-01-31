package ru.practicum.contract.interactionapi.feignclient.fallbackfactory;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductDto;
import ru.practicum.contract.interactionapi.exception.fiegnclient.BadRequestException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.InternalServerErrorException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotFoundException;
import ru.practicum.contract.interactionapi.feignclient.ShoppingStoreClient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class ShoppingStoreFallbackFactory implements FallbackFactory<ShoppingStoreClient> {

    @Override
    public ShoppingStoreClient create(Throwable cause) {
        return new ShoppingStoreClient() {
            @Override
            public Page<ProductDto> getProducts(String category, int from, int size, String sort) {
                throw handleException(cause);
            }

            @Override
            public ProductDto createNewProduct(ProductDto productDto) {
                throw handleException(cause);
            }

            @Override
            public ProductDto updateProduct(ProductDto productDto) {
                throw handleException(cause);
            }

            @Override
            public boolean removeProductFromStore(UUID id) {
                throw handleException(cause);
            }

            @Override
            public boolean setProductQuantityState(UUID productId, String quantityState) {
                throw handleException(cause);
            }

            @Override
            public ProductDto getProduct(UUID productId) {
                throw handleException(cause);
            }

            @Override
            public List<ProductDto> getProducts(Set<UUID> products) {
                throw handleException(cause);
            }
        };
    }

    private RuntimeException handleException(Throwable cause) {
        return switch (cause) {
            case NotFoundException notFoundException -> new NotFoundException(cause.getMessage(),
                    HttpStatus.NOT_FOUND);
            case InternalServerErrorException internalServerErrorException ->
                    new InternalServerErrorException(cause.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            case BadRequestException badRequestException -> new BadRequestException(cause.getMessage(),
                    HttpStatus.BAD_REQUEST);
            case null, default -> new RuntimeException("Произошла ошибка при вызове ShoppingCart service", cause);
        };
    }
}
