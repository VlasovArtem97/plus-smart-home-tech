package ru.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.contract.interactionapi.contract.payment.PaymentOperation;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.dto.payment.PaymentDto;
import ru.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController implements PaymentOperation {

    private final PaymentService paymentService;

    @Override
    public PaymentDto payment(OrderDto orderDto) {
        return paymentService.payment(orderDto);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        return paymentService.getTotalCost(orderDto);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        paymentService.paymentSuccess(paymentId);
    }

    @Override
    public BigDecimal productCost(OrderDto orderDto) {
        return paymentService.productCost(orderDto);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        paymentService.paymentFailed(paymentId);
    }
}
