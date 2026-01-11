package ru.practicum.contract.interactionapi.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.contract.interactionapi.config.Config;
import ru.practicum.contract.interactionapi.contract.shoppingstore.ShoppingStoreOperation;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store", configuration = Config.class)
public interface ShoppingStoreClient extends ShoppingStoreOperation {
}
