package ru.practicum.contract.interactionapi.contract.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.interactionapi.dto.order.CreateNewOrderRequest;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderOperation {

    @GetMapping
    List<OrderDto> getClientOrders(@NotBlank @RequestParam("username") String userName);

    @PutMapping
    OrderDto createNewOrder(@NotNull @Valid @RequestBody CreateNewOrderRequest orderRequest);

    @PostMapping("/return")
    OrderDto productReturn(@NotNull @Valid @RequestBody ProductReturnRequest productReturnRequest);

    @PostMapping("/payment")
    OrderDto payment(@NotNull @RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@NotNull @RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto delivery(@NotNull @RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@NotNull @RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto complete(@NotNull @RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@NotNull @RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@NotNull @RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assembly(@NotNull @RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@NotNull @RequestBody UUID orderId);

}
