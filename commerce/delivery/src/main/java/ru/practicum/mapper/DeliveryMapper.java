package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.contract.interactionapi.dto.delivery.DeliveryDto;
import ru.practicum.model.Delivery;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "deliveryVolume", ignore = true)
    @Mapping(target = "deliveryWeight", ignore = true)
    @Mapping(target = "fragile", ignore = true)
    @Mapping(target = "deliveryState", expression = "java(ru.practicum.contract.interactionapi.dto.delivery.DeliveryState.CREATED)")
    Delivery toDelivery(DeliveryDto deliveryDto);

    DeliveryDto toDeliveryDto(Delivery delivery);

}
