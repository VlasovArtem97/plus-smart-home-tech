package ru.practicum.service;

import ru.practicum.contract.interactionapi.dto.delivery.DeliveryDto;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    void deliverySuccessful(UUID orderId);

    void deliveryPicked(UUID orderId);

    void deliveryFailed(UUID orderId);

    BigDecimal deliveryCost(OrderDto orderDto);
}
