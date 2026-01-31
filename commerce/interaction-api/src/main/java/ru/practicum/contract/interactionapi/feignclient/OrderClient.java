package ru.practicum.contract.interactionapi.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import ru.practicum.contract.interactionapi.config.Config;
import ru.practicum.contract.interactionapi.contract.order.OrderOperation;
import ru.practicum.contract.interactionapi.feignclient.fallbackfactory.OrderFallbackFactory;

@FeignClient(name = "order", path = "/api/v1/order", configuration = Config.class,
        fallbackFactory = OrderFallbackFactory.class)
@Validated
public interface OrderClient extends OrderOperation {
}
