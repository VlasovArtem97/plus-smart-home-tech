package ru.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.contract.interactionapi.dto.delivery.AddressDto;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.dto.warehouse.*;
import ru.practicum.warehouse.exception.NoSpecifiedOrderInOrderBookingException;
import ru.practicum.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.practicum.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.warehouse.mapper.WarehouseProductMapper;
import ru.practicum.warehouse.model.OrderBooking;
import ru.practicum.warehouse.model.WarehouseProduct;
import ru.practicum.warehouse.repository.OrderBookingRepository;
import ru.practicum.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseProductMapper warehouseProductMapper;
    private final OrderBookingRepository orderBookingRepository;

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];


    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest newProduct) {
        log.info("Получен запрос на добавление нового товара на склад: {}", newProduct);
        checkWarehouseProductById(newProduct.getProductId());
        WarehouseProduct warehouseProduct = warehouseRepository.save(
                warehouseProductMapper.toWarehouseProductFromNewProduct(newProduct)
        );
        log.debug("Сохраненный объект: {}", warehouseProduct);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto) {
        log.info("Получен запрос на проверку количество товаров на складе для данной корзины: {}", cartDto);

        Set<UUID> uuids = cartDto.getProducts().keySet();

        List<WarehouseProduct> warehouseProducts = findWarehouseProductByIds(uuids);
        checkProductQuantity(cartDto.getProducts(), warehouseProducts);
        BookedProductsDto productsDto = getBookedProductsDto(warehouseProducts);
        log.debug("Сведения по всем товарам: {}", productsDto);
        return productsDto;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest product) {
        log.info("Получен запрос на добавление определенного количества товара : {}", product);
        List<WarehouseProduct> products = findWarehouseProductByIds(Set.of(product.getProductId()));
        WarehouseProduct warehouseProduct = products.getFirst();
        warehouseProduct.setQuantity(warehouseProduct.getQuantity() + product.getQuantity());
        WarehouseProduct updateProduct = warehouseRepository.save(warehouseProduct);
        log.debug("Количество продукта на складе обновлено: {}", updateProduct);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Получен запрос на получение адреса склада");
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest orderRequest) {
        log.info("Получен запрос на сбор товаров к заказу для подготовки к отправке.");
        //поиск всех продуктов по id на складе
        List<WarehouseProduct> warehouseProducts = findWarehouseProductByIds(orderRequest.getProducts().keySet());
        //проверяем на наличия количества продуктов на складе
        checkProductQuantity(Map.copyOf(orderRequest.getProducts()), warehouseProducts);
        //уменьшаем количество товара на складе
        List<WarehouseProduct> updateWarehouse = updateQuantityProductInWarehouse(warehouseProducts, orderRequest);

        warehouseRepository.saveAll(updateWarehouse);

        OrderBooking orderBooking = orderBookingRepository.save(OrderBooking.builder()
                .orderId(orderRequest.getOrderId())
                .products(orderRequest.getProducts())
                .build());

        log.debug("Сохраненный объект OrderBooking: {}", orderBooking);
        return getBookedProductsDto(updateWarehouse);
    }

    @Override
    public void acceptReturn(Map<UUID, Long> products, UUID orderId) {
        log.info("Получен запрос на возврат товаров: {}", products);
        //поиск всех продуктов по id на складе
        List<WarehouseProduct> warehouseProducts = findWarehouseProductByIds(products.keySet());
        //Находим забронированный заказ
        OrderBooking orderBooking = findOrderBooking(orderId);
        //Проверяем наличия товаров в забронированном заказе
        checkOrderBooking(orderBooking, products);
        //уменьшаем количество забронированных товаров
        lowQuantityInOrderBooking(orderBooking, products);
        //увеличиваем количество товаров на складе
        List<WarehouseProduct> warehouseProductList = warehouseProducts.stream()
                .peek(warehouseProduct -> {
                    Long quantityWarehouse = warehouseProduct.getQuantity();
                    Long quantityOrderRequest = products.get(warehouseProduct.getProductId());
                    warehouseProduct.setQuantity(quantityWarehouse + quantityOrderRequest);
                    log.debug("Количество товара c id [ {} ] на складе увеличилось. Было: [ {} ], стало: [ {} ]",
                            warehouseProduct.getProductId(), quantityWarehouse, warehouseProduct.getQuantity());
                })
                .toList();
        warehouseRepository.saveAll(warehouseProductList);
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest shippedToDeliveryRequest) {
        log.info("Получен запрос на передачу товаров в доставку: {}", shippedToDeliveryRequest);
        OrderBooking orderBooking = findOrderBooking(shippedToDeliveryRequest.getOrderId());
        orderBooking.setDeliveryId(shippedToDeliveryRequest.getDeliveryId());
        OrderBooking updateOrderBooking = orderBookingRepository.save(orderBooking);
        log.debug("Обновленный OrderBooking: {}", updateOrderBooking);
    }

    private void checkWarehouseProductById(UUID productId) {
        log.info("Осуществляется проверка товара на складе c id {}", productId);
        boolean exists = warehouseRepository.existsByProductId(productId);
        if (exists) {
            log.error("Ошибка, товар с id: [ {} ] уже зарегистрирован на складе", productId);
            throw new SpecifiedProductAlreadyInWarehouseException("Ошибка, товар с id: [ " + productId + " ] " +
                    "уже зарегистрирован на складе");
        }
    }

    private List<WarehouseProduct> findWarehouseProductByIds(Set<UUID> productIds) {
        log.info("Осуществляется поиск товаров на складе с id: [ {} ]", productIds);
        List<WarehouseProduct> products = warehouseRepository.findByProductIdIn(productIds);
        if (products.isEmpty()) {
            log.error("Ошибка, товары с ids: [ {} ] отсутствуют на складе", productIds);
            throw new NoSpecifiedProductInWarehouseException("Ошибка, товары с ids: [ " + productIds + " ] " +
                    "отсутствуют на складе");
        }
        Set<UUID> productsId = products.stream()
                .map(WarehouseProduct::getProductId)
                .collect(Collectors.toSet());

        Set<UUID> missingIds = productIds.stream()
                .filter(id -> !productsId.contains(id))
                .collect(Collectors.toSet());

        if (!missingIds.isEmpty()) {
            log.error("Ошибка, товары с ids: [ {} ] отсутствуют на складе", missingIds);
            throw new NoSpecifiedProductInWarehouseException("Ошибка, товары с ids: [ " + missingIds + " ] " +
                    "отсутствуют на складе");
        }

        log.debug("Найденные продукты на складе: {}", products);
        return products;
    }

    private void checkProductQuantity(Map<UUID, Long> products, List<WarehouseProduct> warehouseProducts) {
        log.info("Осуществляется проверка соотношения количество продуктов в корзине и на складе");
        Map<UUID, WarehouseProduct> warehouseProductMap = warehouseProducts.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, wp -> wp));

        List<String> errorMessage = new ArrayList<>();

        for (Map.Entry<UUID, Long> map : products.entrySet()) {
            UUID id = map.getKey();
            Long count = map.getValue();

            WarehouseProduct warehouseProduct = warehouseProductMap.get(id);
            Long quantity = warehouseProduct.getQuantity();
            if (quantity < count) {
                log.error("Ошибка, нехватка количества товара с id: [ {} ] на складе. " +
                        "Количество на складе: [ {} ]. " +
                        "Количество в корзине: [ {} ]", id, quantity, count);
                String message = "Ошибка, нехватка количества товара с id: [ " + id + " ] на складе. " +
                        "Количество в корзине: [ " + count + " ]";
                errorMessage.add(message);
            }
        }

        if (!errorMessage.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(String.join("\n", errorMessage));
        }
    }

    private BookedProductsDto getBookedProductsDto(List<WarehouseProduct> products) {
        double deliveryVolume = 0;
        double deliveryWeight = 0;
        boolean fragile = false;

        for (WarehouseProduct product : products) {
            deliveryVolume += product.getDimension().getVolume();
            deliveryWeight += product.getWeight();
            if (product.getFragile()) {
                fragile = true;
            }
        }
        return BookedProductsDto.builder()
                .deliveryVolume(deliveryVolume)
                .deliveryWeight(deliveryWeight)
                .fragile(fragile)
                .build();
    }

    private List<WarehouseProduct> updateQuantityProductInWarehouse(List<WarehouseProduct> warehouseProducts,
                                                                    AssemblyProductsForOrderRequest orderRequest) {
        return warehouseProducts.stream()
                .peek(warehouseProduct -> {
                    Long quantityWarehouse = warehouseProduct.getQuantity();
                    Long quantityOrderRequest = orderRequest.getProducts().get(warehouseProduct.getProductId());
                    warehouseProduct.setQuantity(quantityWarehouse - quantityOrderRequest);
                    log.debug("Оставшиеся количество товара c id [ {} ] на складе: [ {} ]",
                            warehouseProduct.getProductId(), warehouseProduct.getQuantity());
                })
                .toList();
    }

    private OrderBooking findOrderBooking(UUID orderId) {
        log.info("Начинается процесс поиска забронированного заказа с id: [ {} ]", orderId);
        OrderBooking orderBooking = orderBookingRepository.findByOrderId(orderId).orElseThrow(() -> {
            log.error("Отсутствуют забронированные товары по id заказа: [{}]", orderId);
            return new NoSpecifiedOrderInOrderBookingException("Отсутствует информация о забронированных заказах с id: [" +
                    orderId + "]");
        });
        log.debug("Забронированные товары: {}", orderBooking);
        return orderBooking;
    }

    private void checkOrderBooking(OrderBooking orderBooking, Map<UUID, Long> returnProducts) {
        log.info("Начинается проверка наличия товаров в забронированном заказе. Сущность OrderBooking: {}, " +
                "Возвращаемые товары: {}", orderBooking, returnProducts);
        Map<UUID, Long> orderBookingProducts = orderBooking.getProducts();
        Set<UUID> missingUuid = returnProducts.keySet().stream()
                .filter(aLong -> !orderBookingProducts.containsKey(aLong))
                .collect(Collectors.toSet());
        if (!missingUuid.isEmpty()) {
            log.error("Товары с id: [ {} ] отсутствуют в забронированном заказе", missingUuid);
            throw new NoSpecifiedOrderInOrderBookingException("Товары с id: [ " + missingUuid + " ] " +
                    "отсутствуют в забронированном заказе");
        }
        log.debug("Все продукты соответствуют забронированным");
    }

    private OrderBooking lowQuantityInOrderBooking(OrderBooking orderBooking, Map<UUID, Long> returnProducts) {
        log.info("Начинается процесс уменьшение товаров в забронированном заказе. Сущность OrderBooking: {}, " +
                "Возвращаемые товары: {}", orderBooking, returnProducts);
        returnProducts.forEach((id, quantity) -> {
            Long quantityOrderBooking = orderBooking.getProducts().get(id);
            if (quantity > quantityOrderBooking) {
                log.error("Количество возвращенных товаров превышает забронированных. " +
                                "Количество забронированных товаров: [ {} ]. Количество возвращенных товаров: [ {} ]",
                        quantityOrderBooking, quantity);
                throw new ProductInShoppingCartLowQuantityInWarehouse("Количество возвращенных товаров " +
                        "превышает забронированных. Количество забронированных товаров: [ " +
                        quantityOrderBooking + " ]. Количество возвращенных товаров: [ " + quantity + " ]");
            }
            orderBooking.getProducts().put(id, (quantityOrderBooking - quantity));
            log.debug("Товар с id: {} уменьшен на {}. Новое количество: {}",
                    id, quantity, quantityOrderBooking - quantity);
        });
        OrderBooking updateOrderBooking = orderBookingRepository.save(orderBooking);
        log.debug("Обновлена сущность забронированных товаров: {}", updateOrderBooking);
        return updateOrderBooking;
    }
}
