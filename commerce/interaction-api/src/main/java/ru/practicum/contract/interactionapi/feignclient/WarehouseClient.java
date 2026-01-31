package ru.practicum.contract.interactionapi.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import ru.practicum.contract.interactionapi.config.Config;
import ru.practicum.contract.interactionapi.contract.warehouse.WarehouseOperation;
import ru.practicum.contract.interactionapi.feignclient.fallbackfactory.WarehouseFallbackFactory;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", configuration = Config.class,
        fallbackFactory = WarehouseFallbackFactory.class)
@Validated
public interface WarehouseClient extends WarehouseOperation {
}
