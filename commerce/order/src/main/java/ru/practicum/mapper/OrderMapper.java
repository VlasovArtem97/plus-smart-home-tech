package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.contract.interactionapi.dto.order.CreateNewOrderRequest;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto toOrderDtoFromOrder(Order order);

    @Mapping(source = "shoppingCart.shoppingCartId", target = "shoppingCartId")
    @Mapping(source = "shoppingCart.userName", target = "userName")
    @Mapping(source = "shoppingCart.products", target = "products")
    @Mapping(target = "state", expression = "java(StateOrder.NEW)")
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "deliveryVolume", ignore = true)
    @Mapping(target = "deliveryWeight", ignore = true)
    @Mapping(target = "fragile", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "deliveryPrice", ignore = true)
    @Mapping(target = "productPrice", ignore = true)
    @Mapping(target = "feeTotal", ignore = true)
    Order toOrder(CreateNewOrderRequest request);

}
