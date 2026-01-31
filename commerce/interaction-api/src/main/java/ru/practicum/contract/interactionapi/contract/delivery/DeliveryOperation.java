package ru.practicum.contract.interactionapi.contract.delivery;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.contract.interactionapi.dto.delivery.DeliveryDto;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryOperation {

    @PutMapping
    DeliveryDto createDelivery(@NotNull @RequestBody DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void deliverySuccessful(@NotNull @RequestBody UUID orderId);

    @PostMapping("/picked")
    void deliveryPicked(@NotNull @RequestBody UUID orderId);

    @PostMapping("/failed")
    void deliveryFailed(@NotNull @RequestBody UUID orderId);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@NotNull @RequestBody OrderDto orderDto);
}
