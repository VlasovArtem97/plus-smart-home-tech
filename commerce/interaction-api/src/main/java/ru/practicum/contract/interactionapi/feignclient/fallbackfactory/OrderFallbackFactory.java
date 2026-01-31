package ru.practicum.contract.interactionapi.feignclient.fallbackfactory;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.contract.interactionapi.dto.order.CreateNewOrderRequest;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.dto.order.ProductReturnRequest;
import ru.practicum.contract.interactionapi.exception.fiegnclient.BadRequestException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.InternalServerErrorException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotAuthorizedException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotFoundException;
import ru.practicum.contract.interactionapi.feignclient.OrderClient;

import java.util.List;
import java.util.UUID;

@Component
public class OrderFallbackFactory implements FallbackFactory<OrderClient> {

    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {
            @Override
            public List<OrderDto> getClientOrders(String userName) {
                throw handleException(cause);
            }

            @Override
            public OrderDto createNewOrder(CreateNewOrderRequest orderRequest) {
                throw handleException(cause);
            }

            @Override
            public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
                throw handleException(cause);
            }

            @Override
            public OrderDto payment(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public OrderDto paymentFailed(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public OrderDto delivery(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public OrderDto deliveryFailed(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public OrderDto complete(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public OrderDto calculateTotalCost(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public OrderDto calculateDeliveryCost(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public OrderDto assembly(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public OrderDto assemblyFailed(UUID orderId) {
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
