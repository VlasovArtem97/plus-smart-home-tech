package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.contract.interactionapi.dto.delivery.DeliveryDto;
import ru.practicum.contract.interactionapi.dto.delivery.DeliveryState;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.dto.warehouse.ShippedToDeliveryRequest;
import ru.practicum.contract.interactionapi.exception.commerce.NotEnoughInfoInOrderToCalculateException;
import ru.practicum.contract.interactionapi.feignclient.OrderClient;
import ru.practicum.contract.interactionapi.feignclient.WarehouseClient;
import ru.practicum.exception.IncorrectDeliveryStateException;
import ru.practicum.exception.NoDeliveryFoundException;
import ru.practicum.exception.OrderExistsInDeliveryException;
import ru.practicum.mapper.DeliveryMapper;
import ru.practicum.model.Delivery;
import ru.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static java.math.BigDecimal.valueOf;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;
    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;

    private static final BigDecimal RATIO_ADDRESS_ONE = BigDecimal.valueOf(1.0);
    private static final BigDecimal RATIO_ADDRESS_TWO = BigDecimal.valueOf(2.0);
    private static final BigDecimal RATIO_FRAGILE_TRUE = BigDecimal.valueOf(0.2);
    private static final BigDecimal RATIO_VOLUME = BigDecimal.valueOf(0.2);
    private static final BigDecimal RATIO_WEIGHT = BigDecimal.valueOf(0.3);
    private static final BigDecimal RATIO_ADDRESS_DELIVERY = BigDecimal.valueOf(0.2);
    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};

    @Override
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        log.info("Получен запрос на создание доставки: {}", deliveryDto);
        // Проверяем в наличии заказа по id в доставке
        checkOrderIdInDelivery(deliveryDto.getOrderId());

        //Проверяем статус, он должен быть равен null. Данный метод устанавливает статус
        if (deliveryDto.getDeliveryState() != null) {
            log.error("При создании доставки, статус не должен указываться: {}", deliveryDto.getDeliveryState());
            throw new IncorrectDeliveryStateException("При создании доставки, статус не должен указываться: " +
                    deliveryDto.getDeliveryState());
        }
        Delivery delivery = deliveryMapper.toDelivery(deliveryDto);

        DeliveryDto dto = deliveryMapper.toDeliveryDto(deliveryRepository.save(delivery));
        log.debug("Доставка создана: {}", dto);
        return dto;
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        log.info("Получен запрос об успешной доставки товара заказа: {}", orderId);
        //ищем доставку по OrderId
        Delivery delivery = findDelivery(orderId);
        //Проверяем статус
        if (delivery.getDeliveryState() != DeliveryState.IN_PROGRESS) {
            log.error("Статус доставки некорректный для успешной доставки: {}. Должно быть: {}",
                    delivery.getDeliveryState(), DeliveryState.IN_PROGRESS);
            throw new IncorrectDeliveryStateException("Статус доставки некорректный для успешной доставки: " +
                    delivery.getDeliveryState() + ". Должно быть: " + DeliveryState.IN_PROGRESS);
        }
        //устанавливаем статус и сохраняем
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        //отправляем запрос в order об успешности доставки
        orderClient.delivery(orderId);
        log.debug("Установлен новый статус успешности доставки для заказа: {}, Статус: {}", orderId,
                delivery.getDeliveryState());
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        log.info("Получен запрос на получения товара в доставку по заказу: {}", orderId);
        Delivery delivery = findDelivery(orderId);
        //проверяем статус
        if (delivery.getDeliveryState() != DeliveryState.CREATED) {
            log.error("Статус доставки некорректный для получения товаров: {}. Должно быть: {}",
                    delivery.getDeliveryState(), DeliveryState.CREATED);
            throw new IncorrectDeliveryStateException("Статус доставки некорректный для получения товаров: " +
                    delivery.getDeliveryState() + ". Должно быть: " + DeliveryState.CREATED);
        }

        //обращаемся к складу для информирования того, что товары переданы в доставку
        warehouseClient.shippedToDelivery(ShippedToDeliveryRequest.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .build());

        //меняем статус и сохраняем
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
        //изменяем статус Order на ASSEMBLED
        orderClient.assembly(orderId);

        log.debug("Установлен новый статус передачи товаров в доставку для заказа: {}, Статус: {}",
                orderId, delivery.getDeliveryState());
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        log.info("Получен запрос на неудачное вручение товара заказа: {}", orderId);
        Delivery delivery = findDelivery(orderId);
        //Проверяем статус
        if (delivery.getDeliveryState() != DeliveryState.IN_PROGRESS) {
            log.error("Статус доставки некорректный для неудачной доставки: {}. Должно быть: {}",
                    delivery.getDeliveryState(), DeliveryState.IN_PROGRESS);
            throw new IncorrectDeliveryStateException("Статус доставки некорректный для неудачной доставки: " +
                    delivery.getDeliveryState() + ". Должно быть: " + DeliveryState.IN_PROGRESS);
        }
        //Устанавливаем статус
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        //Обращаемся к Order для установки статуса "Неудачная доставка
        orderClient.deliveryFailed(orderId);
        log.debug("Установлен новый статус неудачной доставки для заказа: {}, Статус: {}",
                orderId, delivery.getDeliveryState());
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        log.info("Получен запрос на Расчёт полной стоимости доставки заказа: {}", orderDto);
        Delivery delivery = findDelivery(orderDto.getOrderId());

        if (orderDto.getDeliveryVolume() == null || orderDto.getDeliveryWeight() == null || orderDto.getFragile() == null) {
            log.error("Недостаточно информации для подсчета стоимости доставки. Проверить вес, объем и хрупкость");
            throw new NotEnoughInfoInOrderToCalculateException("Недостаточно информации для подсчета стоимости доставки");
        }
        if (delivery.getDeliveryVolume() == null && delivery.getDeliveryWeight() == null && delivery.getFragile() == null) {
            delivery.setDeliveryVolume(orderDto.getDeliveryVolume());
            delivery.setDeliveryWeight(orderDto.getDeliveryWeight());
            delivery.setFragile(orderDto.getFragile());
            deliveryRepository.save(delivery);
        }

        //Рассчитываем в зависимости от склада
        BigDecimal totalPriceDelivery = valueOf(5);
        if (delivery.getFromAddress().getStreet().equals(ADDRESSES[0])) {
            totalPriceDelivery = totalPriceDelivery.add(totalPriceDelivery.multiply(RATIO_ADDRESS_ONE));
        }
        if (delivery.getFromAddress().getStreet().equals(ADDRESSES[1])) {
            totalPriceDelivery = totalPriceDelivery.add(totalPriceDelivery.multiply(RATIO_ADDRESS_TWO));
        }
        //Рассчитываем в зависимости от хрупкости
        if (orderDto.getFragile()) {
            totalPriceDelivery = totalPriceDelivery.add(totalPriceDelivery.multiply(RATIO_FRAGILE_TRUE));
        }
        //Рассчитываем в зависимости от веса
        totalPriceDelivery = totalPriceDelivery.add(BigDecimal.valueOf(orderDto.getDeliveryWeight())
                .multiply(RATIO_WEIGHT));
        //Рассчитываем в зависимости от объема
        totalPriceDelivery = totalPriceDelivery.add(BigDecimal.valueOf(orderDto.getDeliveryVolume())
                .multiply(RATIO_VOLUME));
        //Рассчитываем в зависимости от расположения адреса и склада доставки
        if (!delivery.getToAddress().getStreet().equals(delivery.getFromAddress().getStreet())) {
            totalPriceDelivery = totalPriceDelivery.add(totalPriceDelivery.multiply(RATIO_ADDRESS_DELIVERY));
        }
        log.debug("Полученная стоимость доставки: {}", totalPriceDelivery);
        return totalPriceDelivery;
    }

    private void checkOrderIdInDelivery(UUID orderId) {
        Optional<Delivery> delivery = deliveryRepository.findByOrderId(orderId);
        if (delivery.isPresent()) {
            log.error("Такой Order c id: [ {} ] уже существует в Delivery", orderId);
            throw new OrderExistsInDeliveryException("Order c id: [" + orderId + "] уже существует в доставке");
        }
    }

    private Delivery findDelivery(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId).orElseThrow(() -> {
            log.error("Доставка с OrderId: [ {} ] не найдена", orderId);
            return new NoDeliveryFoundException("Доставка с OrderId: [" + orderId + "] не найдена");
        });
    }
}
