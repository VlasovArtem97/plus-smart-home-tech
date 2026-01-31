package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.contract.interactionapi.dto.delivery.AddressDto;
import ru.practicum.contract.interactionapi.dto.delivery.DeliveryDto;
import ru.practicum.contract.interactionapi.dto.order.CreateNewOrderRequest;
import ru.practicum.contract.interactionapi.dto.order.OrderDto;
import ru.practicum.contract.interactionapi.dto.order.ProductReturnRequest;
import ru.practicum.contract.interactionapi.dto.payment.PaymentDto;
import ru.practicum.contract.interactionapi.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.practicum.contract.interactionapi.dto.warehouse.BookedProductsDto;
import ru.practicum.contract.interactionapi.exception.commerce.IncorrectOrderStateException;
import ru.practicum.contract.interactionapi.exception.commerce.NoOrderFoundException;
import ru.practicum.contract.interactionapi.exception.commerce.NotAuthorizedUserException;
import ru.practicum.contract.interactionapi.exception.commerce.NotEnoughInfoInOrderToCalculateException;
import ru.practicum.contract.interactionapi.feignclient.DeliveryClient;
import ru.practicum.contract.interactionapi.feignclient.PaymentClient;
import ru.practicum.contract.interactionapi.feignclient.WarehouseClient;
import ru.practicum.exception.NotFoundUserException;
import ru.practicum.mapper.OrderMapper;
import ru.practicum.model.Order;
import ru.practicum.model.StateOrder;
import ru.practicum.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Transactional(readOnly = true)
    @Override
    public List<OrderDto> getClientOrders(String userName) {
        log.info("Получен запрос на получение списка заказов от пользователя с именем [ {} ]", userName);
        checkUserName(userName);
        return findOrderByUserName(userName);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest orderRequest) {
        log.info("Получен запрос на добавление нового заказа: {}", orderRequest);

        //Сохраняем Order в БД для получения id
        Order order = orderMapper.toOrder(orderRequest);
        Order createOrder = orderRepository.save(order);
        log.debug("Первоначально сохраненный объект: {}", createOrder);

        //Начинаем процесс сборки, проверки наличия товаров на складе и бронирование
        BookedProductsDto bookedProductsDto = warehouseClient.assemblyProductsForOrder(
                AssemblyProductsForOrderRequest.builder()
                        .orderId(createOrder.getOrderId())
                        .products(createOrder.getProducts())
                        .build()
        );

        //устанавливаем общий вес, объем и хрупкость
        createOrder.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        createOrder.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        createOrder.setFragile(bookedProductsDto.getFragile());

        //Получаем адрес склада
        AddressDto addressDto = warehouseClient.getWarehouseAddress();

        //Создаем доставку
        DeliveryDto dto = deliveryClient.createDelivery(DeliveryDto.builder()
                .orderId(createOrder.getOrderId())
                .fromAddress(addressDto)
                .toAddress(orderRequest.getDeliveryAddress())
                .build());
        //Присваиваем DeliveryId
        createOrder.setDeliveryId(dto.getDeliveryId());

        //преобразуем в dto
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(createOrder);

        //рассчитываем стоимость доставки
        orderDto.setDeliveryPrice(deliveryClient.deliveryCost(orderDto));

        //создаем платеж сразу
        PaymentDto paymentDto = paymentClient.payment(orderDto);

        //устанавливаем значения
        createOrder.setPaymentId(paymentDto.getPaymentId());
        createOrder.setTotalPrice(paymentDto.getTotalPayment());
        createOrder.setFeeTotal(paymentDto.getFeeTotal());
        createOrder.setProductPrice(paymentDto.getProductPrice());

        //Устанавливаем статус ожидания оплаты
        createOrder.setState(StateOrder.ON_PAYMENT);

        OrderDto finalOrder = orderMapper.toOrderDtoFromOrder(createOrder);
        log.debug("Создана новая заявка: {}", finalOrder);
        return orderDto;
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        log.info("Получен запрос на возврат товаров: {}", productReturnRequest);
        //ищем order по id
        Order order = getOrderById(productReturnRequest.getOrderId());
        //проверяем по статусу, что продукты до этого не были возвращены
        if (order.getState() == StateOrder.PRODUCT_RETURNED) {
            log.error("Продукты уже были возвращены (статус соответствует): {}", order);
            throw new IncorrectOrderStateException("Продукты уже были возвращены на склад. Статус: " + order.getState());
        }
        //оформляем возврат
        warehouseClient.acceptReturn(productReturnRequest.getProducts(), order.getOrderId());
        //изменяем статус
        order.setState(StateOrder.PRODUCT_RETURNED);
        //сохраняем и возвращаем
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
        log.debug("Продукты успешно возвращены: {}", orderDto);
        return orderDto;
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("Получен запрос на оплату заказа: [ {} ]", orderId);
        Order order = getOrderById(orderId);
        //проверяем статус
        if (order.getState() != StateOrder.ON_PAYMENT) {
            log.error("Статус заказа отличается. Должно: [ {} ]. Получаем: [ {} ]", StateOrder.ON_PAYMENT, order.
                    getState());
            throw new IncorrectOrderStateException("Для успешной оплаты необходимо, чтобы заказ был со статусом: [ " +
                    StateOrder.ON_PAYMENT + " ]. А у данного заказа статус: [ " + order.getState() + " ]");
        }
        order.setState(StateOrder.PAID);
        OrderDto updateState = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
        log.debug("Статус заказа изменен: {}", updateState);
        return updateState;
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Получен запрос на неудачную оплату заказа: [ {} ]", orderId);
        Order order = getOrderById(orderId);
        //проверяем статус
        if (order.getState() != StateOrder.ON_PAYMENT) {
            log.error("Статус заказа отличается. Должно: [{}]. Получаем: [{}]", StateOrder.ON_PAYMENT, order.
                    getState());
            throw new IncorrectOrderStateException("Для неуспешной оплаты необходимо, чтобы заказ был со статусом: [ " +
                    StateOrder.ON_PAYMENT + " ]. А у данного заказа статус: [ " + order.getState() + " ]");
        }
        order.setState(StateOrder.PAYMENT_FAILED);
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
        log.debug("Статус заявки изменен на PAYMENT_FAILED: {}", orderDto);
        return orderDto;
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("Получен запрос на успешное выполнение доставки по заказу: {}", orderId);
        Order order = getOrderById(orderId);
        if (order.getState() != StateOrder.ON_DELIVERY) {
            log.error("Статус заказа отличается. Должно: [{}]. Получаем: [ {} ]", StateOrder.ON_DELIVERY, order.
                    getState());
            throw new IncorrectOrderStateException("Для успешной доставки необходимо, чтобы заказ был со статусом: [ " +
                    StateOrder.ON_DELIVERY + " ]. А у данного заказа статус: [ " + order.getState() + " ]");
        }
        order.setState(StateOrder.DELIVERED);
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
        log.debug("Статус заявки изменен на DELIVERED: {}", orderDto);
        return orderDto;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.debug("Получен запрос на неудачную доставку заказа: {}", orderId);
        Order order = getOrderById(orderId);
        if (order.getState() != StateOrder.ON_DELIVERY) {
            log.error("Статус заказа отличается. Должно: [ {} ]. Получаем: [{}]", StateOrder.ON_DELIVERY, order.
                    getState());
            throw new IncorrectOrderStateException("Для неуспешной доставки необходимо, чтобы заказ был со статусом: [ " +
                    StateOrder.ON_DELIVERY + " ]. А у данного заказа статус: [ " + order.getState() + " ]");
        }
        order.setState(StateOrder.DELIVERY_FAILED);
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
        log.debug("Статус заявки изменен на DELIVERY_FAILED: {}", orderDto);
        return orderDto;

    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.debug("Получен запрос на завершение заказа: {}", orderId);
        Order order = getOrderById(orderId);
        if (order.getState() != StateOrder.DELIVERED) {
            log.error("Статус заказа отличается. Должно: [  {} ]. Получаем: [{}]", StateOrder.DELIVERED, order.
                    getState());
            throw new IncorrectOrderStateException("Для завершения всех этапов заказа необходимо, чтобы заказ был " +
                    "со статусом: [ " + StateOrder.DELIVERED + " ]. А у данного заказа статус: [ " +
                    order.getState() + " ]");
        }
        order.setState(StateOrder.COMPLETED);
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
        log.debug("Статус заявки изменен на COMPLETED: {}", orderDto);
        return orderDto;
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Получен запрос на полный подсчет стоимости заказа вместе с доставкой: {}", orderId);
        Order order = getOrderById(orderId);
       /* зараннее проверяем поле с deliveryPrice, чтобы убедиться, что оно не пустое
       и не обращаться лишний раз к другому сервису
        */
        if (order.getDeliveryPrice() == null) {
            log.error("Стоимость доставки в заказе не указана (deliveryPrice)");
            throw new NotEnoughInfoInOrderToCalculateException("Стоимость доставки не указана (deliveryPrice");
        }
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(order);
        //проверяем поле с полной стоимостью товара
        if (order.getTotalPrice() == null) {
            BigDecimal totalPrice = paymentClient.getTotalCost(orderMapper.toOrderDtoFromOrder(order));
            //устанавливаем значение
            order.setTotalPrice(totalPrice);
            OrderDto updateTotalPrice = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
            log.debug("Поле totalPrice обновлено: {}", updateTotalPrice);
            return updateTotalPrice;
        }
        log.debug("Поле totalPrice было ранее подсчитано: {}", orderDto);
        return orderDto;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Получена информация на подсчет стоимости доставки заказа: {}", orderId);
        Order order = getOrderById(orderId);
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(order);
        if (orderDto.getDeliveryPrice() == null) {
            //узнаем цену доставки
            BigDecimal deliveryCost = deliveryClient.deliveryCost(orderDto);
            //сохраняем значение
            order.setDeliveryPrice(deliveryCost);
            OrderDto updateDeliveryPrice = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
            log.debug("Поле DeliveryPrice обновлено в заказе: {}", updateDeliveryPrice);
            return updateDeliveryPrice;
        }
        log.debug("Поле DeliveryPrice было ранее подсчитано: {}", orderDto);
        return orderDto;
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Получен запрос на сборку заказа: {}", orderId);
        Order order = getOrderById(orderId);
        //проверяем статус
        if (order.getState() == StateOrder.PAID) {
            log.error("Статус заказа отличается. Должно: [  {}  ]. Получаем: [  {}  ]", StateOrder.PAID, order.
                    getState());
            throw new IncorrectOrderStateException("Для успешной сборки заказа необходимо, чтобы заказ был со статусом: [ " +
                    StateOrder.PAID + " ]. А у данного заказа статус: [ " + order.getState() + " ]");
        }
        order.setState(StateOrder.ASSEMBLED);
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
        log.debug("Cтатус заказа изменен на удачную сборку: {}", orderDto);
        return orderDto;
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Получен запрос на изменения статуса заказа на неудачную сборку: {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(StateOrder.ASSEMBLY_FAILED);
        OrderDto orderDto = orderMapper.toOrderDtoFromOrder(orderRepository.save(order));
        log.debug("Cтатус заказа изменен на неудачную сборку: {}", orderDto);
        return orderDto;
    }

    private List<OrderDto> findOrderByUserName(String userName) {
        List<Order> orders = orderRepository.findByUserName(userName);
        if (orders.isEmpty()) {
            log.error("Заказ пользователя с именем: [ {} ] отсутствует", userName);
            throw new NotFoundUserException("У пользователя с именем : [ " + userName + " ] " +
                    "отсутствует заказ");
        }
        List<OrderDto> orderDto = orders.stream()
                .map(orderMapper::toOrderDtoFromOrder)
                .toList();
        log.debug("Список заказов пользователя [ {} ]: {}", userName, orderDto);
        return orderDto;
    }

    private void checkUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            log.error("Отсутствует имя пользователя");
            throw new NotAuthorizedUserException("Отсутствует имя пользователя");
        }
    }

    private Order getOrderById(UUID orderId) {
        log.info("Начинается процесс поиска заказа с id: [{}]", orderId);
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(() -> {
            log.error("Заказ с id: [ {} ] не найден", orderId);
            return new NoOrderFoundException("Заказ с id: [" + orderId + "] не найден");
        });
        log.debug("Найденный заказ: {}", order);
        return order;
    }

}
