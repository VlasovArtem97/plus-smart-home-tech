package ru.practicum.contract.interactionapi.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.contract.interactionapi.config.Config;
import ru.practicum.contract.interactionapi.contract.shoppingcart.ShoppingCartOperation;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart", configuration = Config.class)
public interface ShoppingCartClient extends ShoppingCartOperation {
}
