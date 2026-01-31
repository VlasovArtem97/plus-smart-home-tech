package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.contract.interactionapi.contract.order.OrderOperation;
import ru.practicum.contract.interactionapi.dto.order.CreateNewOrderRequest;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.dto.order.ProductReturnRequest;
import ru.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/order")
@RequiredArgsConstructor
@Validated
public class OrderController implements OrderOperation {

    private final OrderService orderService;

    @Override
    public List<OrderDto> getClientOrders(String userName) {
        return orderService.getClientOrders(userName);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest orderRequest) {
        return orderService.createNewOrder(orderRequest);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        return null;
    }

    @Override
    public OrderDto payment(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto complete(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        return null;
    }
}
