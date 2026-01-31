package ru.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.dto.order.StateOrderDto;
import ru.practicum.contract.interactionapi.dto.payment.PaymentDto;
import ru.practicum.contract.interactionapi.dto.payment.PaymentStates;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductDto;
import ru.practicum.contract.interactionapi.exception.commerce.IncorrectOrderStateException;
import ru.practicum.contract.interactionapi.exception.commerce.NotEnoughInfoInOrderToCalculateException;
import ru.practicum.contract.interactionapi.feignclient.OrderClient;
import ru.practicum.contract.interactionapi.feignclient.ShoppingStoreClient;
import ru.practicum.payment.exception.IncorrectPaymentStateException;
import ru.practicum.payment.exception.NoPaymentFoundException;
import ru.practicum.payment.exception.OrderExistsInPaymentException;
import ru.practicum.payment.mapper.PaymentMapper;
import ru.practicum.payment.model.Payment;
import ru.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    private static final BigDecimal RATIO_NDS = BigDecimal.valueOf(0.1);

    @Override
    public PaymentDto payment(OrderDto orderDto) {
        log.info("Получен запрос на Формирование оплаты для заказа: {}", orderDto);
        //Проверяем, что заявка только что создана
        if (orderDto.getState() != StateOrderDto.NEW) {
            log.error("Статус заявки должен быть: {}. В запросе приходит: {}", StateOrderDto.NEW, orderDto.getState());
            throw new IncorrectOrderStateException("Статус заявки должен быть: " + StateOrderDto.NEW + ". " +
                    "В запрос приходит: " + orderDto.getState());
        }
        //проверка, что такого заказа в платежах не существует
        checkPaymentByOrderId(orderDto.getOrderId());
        //пока не знаю, должно устанавливаться значения сразу или по мере добавления
        if (orderDto.getDeliveryPrice() == null) {
            log.error("Стоимость доставки в заказе не указана");
            throw new NotEnoughInfoInOrderToCalculateException("Стоимость доставки не указана");
        }
        //стоимость продуктов
        BigDecimal productPrice = productCost(orderDto);
        //устанавливаем значение для стоимости продуктов
        orderDto.setProductPrice(productPrice);
        //Узнаем значение налога
        BigDecimal feeTotal = productPrice.multiply(RATIO_NDS);
        //устанавливаем значение налога
        orderDto.setFeeTotal(feeTotal);
        //полная стоимость заказа
        BigDecimal totalPrice = getTotalCost(orderDto);
        orderDto.setTotalPrice(totalPrice);

        Payment payment = paymentRepository.save(paymentMapper.toPayment(orderDto));
        PaymentDto paymentDto = paymentMapper.toPaymentDto(payment);
        log.debug("Сохраненный Payment: {}", paymentDto);
        return paymentDto;
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        log.info("Получен запрос на полную стоимость заказа: {}", orderDto);
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (orderDto.getDeliveryPrice() == null) {
            log.error("Стоимость доставки не указана");
            throw new NotEnoughInfoInOrderToCalculateException("Стоимость доставки не указана");
        }
        if (orderDto.getProductPrice() == null) {
            totalPrice = totalPrice.add(productCost(orderDto));
        } else {
            totalPrice = totalPrice.add(orderDto.getProductPrice());
        }
        //добавляем ндс
        totalPrice = totalPrice.add(totalPrice.multiply(RATIO_NDS));
        //Добавляем доставку
        totalPrice = totalPrice.add(orderDto.getDeliveryPrice());
        log.debug("Итоговая стоимость заказа: {}", totalPrice);
        return totalPrice;
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        log.info("Получен запрос об успешной оплате платежа с id: {}", paymentId);
        Payment payment = checkPaymentById(paymentId);
        payment.setPaymentState(PaymentStates.SUCCESS);
        paymentRepository.save(payment);

        //отправляем в сервис Order для изменения статуса
        orderClient.payment(paymentId);

        log.debug("Заказ успешно оплачен: {}", payment.getPaymentState());
    }

    @Override
    public BigDecimal productCost(OrderDto orderDto) {
        log.info("Получен запрос на расчет стоимости товаров: {}", orderDto);
        List<ProductDto> productDtos = shoppingStoreClient.getProducts(orderDto.getProducts().keySet());

        BigDecimal totalPrice = new BigDecimal(BigInteger.ZERO);
        Map<UUID, BigDecimal> bigDecimalMap = productDtos.stream()
                .collect(Collectors.toMap(
                        ProductDto::getProductId,
                        ProductDto::getPrice
                ));

        for (Map.Entry<UUID, Long> map : orderDto.getProducts().entrySet()) {
            UUID productId = map.getKey();
            if (bigDecimalMap.containsKey(productId)) {
                BigDecimal bigDecimal = bigDecimalMap.get(productId);
                bigDecimal = bigDecimal.multiply(BigDecimal.valueOf(map.getValue()));
                totalPrice = totalPrice.add(bigDecimal);
            }
        }

        log.debug("Итоговая стоимость товаров: [ {} ]", totalPrice);
        return totalPrice;
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("Получен запрос об неудачной оплате заказа: {}", paymentId);
        Payment payment = checkPaymentById(paymentId);

        if (payment.getPaymentState() == PaymentStates.SUCCESS) {
            log.error("Заказ уже оплачен: {}", payment.getPaymentState());
            throw new IncorrectPaymentStateException("Заказ уже оплачен: " + payment.getPaymentState());
        }

        payment.setPaymentState(PaymentStates.FAILED);
        paymentRepository.save(payment);
        orderClient.paymentFailed(paymentId);
        log.debug("Статус платежа об неудачной оплате обновлен: {}", payment.getPaymentState());
    }

    private void checkPaymentByOrderId(UUID orderId) {
        Optional<Payment> delivery = paymentRepository.findByOrderId(orderId);
        if (delivery.isPresent()) {
            log.error("Такой Order c id: [ {} ] уже существует в Payment", orderId);
            throw new OrderExistsInPaymentException("Order c id: [" + orderId + "] уже существует в Payment");
        }
    }

    private Payment checkPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId).orElseThrow(() -> {
            log.error("Not Found PaymentId: {}", paymentId);
            return new NoPaymentFoundException("Payment не найден по id: " + paymentId);
        });
        log.debug("Найден Payment: {}", payment);
        return payment;
    }
}
