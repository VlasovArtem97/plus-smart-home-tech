package ru.practicum.contract.interactionapi.contract.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperation {

    @PostMapping
    PaymentDto payment(@NotNull @Valid @RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    BigDecimal getTotalCost(@NotNull @Valid @RequestBody OrderDto orderDto);

    @PostMapping("/refund")
    void paymentSuccess(@NotNull @RequestBody UUID paymentId);

    @PostMapping("/productCost")
    BigDecimal productCost(@NotNull @Valid @RequestBody OrderDto orderDto);

    @PostMapping("/failed")
    void paymentFailed(@NotNull @RequestBody UUID paymentId);

}
