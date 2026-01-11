package ru.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.contract.interactionapi.dto.warehouse.NewProductInWarehouseRequest;
import ru.practicum.warehouse.model.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseProductMapper {

    @Mapping(target = "quantity", ignore = true)
    WarehouseProduct toWarehouseProductFromNewProduct(NewProductInWarehouseRequest product);
}
