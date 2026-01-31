package ru.practicum.contract.interactionapi.feignclient.fallbackfactory;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.contract.interactionapi.dto.delivery.DeliveryDto;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.exception.fiegnclient.BadRequestException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.InternalServerErrorException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotAuthorizedException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotFoundException;
import ru.practicum.contract.interactionapi.feignclient.DeliveryClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class DeliveryFallbackFactory implements FallbackFactory<DeliveryClient> {

    @Override
    public DeliveryClient create(Throwable cause) {
        return new DeliveryClient() {
            @Override
            public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
                throw handleException(cause);
            }

            @Override
            public void deliverySuccessful(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public void deliveryPicked(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public void deliveryFailed(UUID orderId) {
                throw handleException(cause);
            }

            @Override
            public BigDecimal deliveryCost(OrderDto orderDto) {
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
