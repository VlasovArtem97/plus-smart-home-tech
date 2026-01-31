package ru.practicum.contract.interactionapi.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import ru.practicum.contract.interactionapi.config.Config;
import ru.practicum.contract.interactionapi.contract.delivery.DeliveryOperation;
import ru.practicum.contract.interactionapi.feignclient.fallbackfactory.DeliveryFallbackFactory;

@FeignClient(name = "delivery", path = "/api/v1/delivery", configuration = Config.class,
        fallbackFactory = DeliveryFallbackFactory.class)
@Validated
public interface DeliveryClient extends DeliveryOperation {
}
