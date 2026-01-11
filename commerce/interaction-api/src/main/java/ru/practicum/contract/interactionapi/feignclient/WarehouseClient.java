package ru.practicum.contract.interactionapi.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.contract.interactionapi.config.Config;
import ru.practicum.contract.interactionapi.contract.warehouse.WarehouseOperation;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", configuration = Config.class)
public interface WarehouseClient extends WarehouseOperation {
}
