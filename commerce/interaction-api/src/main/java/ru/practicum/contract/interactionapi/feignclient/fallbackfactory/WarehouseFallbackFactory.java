package ru.practicum.contract.interactionapi.feignclient.fallbackfactory;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.contract.interactionapi.dto.delivery.AddressDto;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.dto.warehouse.*;
import ru.practicum.contract.interactionapi.exception.fiegnclient.BadRequestException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.InternalServerErrorException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotFoundException;
import ru.practicum.contract.interactionapi.feignclient.WarehouseClient;

import java.util.Map;
import java.util.UUID;

@Component
public class WarehouseFallbackFactory implements FallbackFactory<WarehouseClient> {

    @Override
    public WarehouseClient create(Throwable cause) {
        return new WarehouseClient() {
            @Override
            public void newProductInWarehouse(NewProductInWarehouseRequest newProduct) {
                throw handleException(cause);
            }

            @Override
            public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto) {
                throw handleException(cause);
            }

            @Override
            public void addProductToWarehouse(AddProductToWarehouseRequest product) {
                throw handleException(cause);
            }

            @Override
            public AddressDto getWarehouseAddress() {
                throw handleException(cause);
            }

            @Override
            public void shippedToDelivery(ShippedToDeliveryRequest shippedToDeliveryRequest) {
                throw handleException(cause);
            }

            @Override
            public void acceptReturn(Map<UUID, Long> products, UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest orderRequest) {
                throw handleException(cause);
            }
        };
    }

    private RuntimeException handleException(Throwable cause) {
        return switch (cause) {
            case NotFoundException notFoundException -> new NotFoundException(cause.getMessage(), HttpStatus.NOT_FOUND);
            case BadRequestException badRequestException -> new BadRequestException(cause.getMessage(),
                    HttpStatus.BAD_REQUEST);
            case InternalServerErrorException internalServerErrorException ->
                    new InternalServerErrorException(cause.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            case null, default -> new RuntimeException("Произошла ошибка при вызове ShoppingCart service", cause);
        };
    }
}
