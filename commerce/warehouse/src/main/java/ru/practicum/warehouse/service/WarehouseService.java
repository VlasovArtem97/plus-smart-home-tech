package ru.practicum.warehouse.service;

import ru.practicum.contract.interactionapi.dto.delivery.AddressDto;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void newProductInWarehouse(NewProductInWarehouseRequest newProduct);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto);

    void addProductToWarehouse(AddProductToWarehouseRequest product);

    AddressDto getWarehouseAddress();

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest orderRequest);

    void acceptReturn(Map<UUID, Long> products, UUID orderId);

    void shippedToDelivery(ShippedToDeliveryRequest shippedToDeliveryRequest);
}
