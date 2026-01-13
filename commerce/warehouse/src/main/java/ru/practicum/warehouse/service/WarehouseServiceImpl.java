package ru.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.contract.interactionapi.dto.warehouse.AddressDto;
import ru.practicum.contract.interactionapi.dto.warehouse.BookedProductsDto;
import ru.practicum.contract.interactionapi.dto.warehouse.NewProductInWarehouseRequest;
import ru.practicum.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.practicum.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.warehouse.mapper.WarehouseProductMapper;
import ru.practicum.warehouse.model.WarehouseProduct;
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
}
