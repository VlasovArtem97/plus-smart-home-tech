package ru.practicum.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.dto.payment.PaymentDto;
import ru.practicum.payment.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDto toPaymentDto(Payment payment);

    @Mapping(source = "totalPrice", target = "totalPayment")
    @Mapping(source = "deliveryPrice", target = "deliveryTotal")
    @Mapping(target = "paymentState", expression = "java(ru.practicum.contract.interactionapi.dto.payment.PaymentStates.PENDING)")
    Payment toPayment(OrderDto orderDto);
}
