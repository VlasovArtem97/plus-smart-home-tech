package ru.practicum.contract.interactionapi.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.contract.interactionapi.config.Config;
import ru.practicum.contract.interactionapi.contract.shoppingcart.ShoppingCartOperation;
import ru.practicum.contract.interactionapi.feignclient.fallbackfactory.ShoppingCartFallbackFactory;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart", configuration = Config.class,
        fallbackFactory = ShoppingCartFallbackFactory.class)
public interface ShoppingCartClient extends ShoppingCartOperation {
}
