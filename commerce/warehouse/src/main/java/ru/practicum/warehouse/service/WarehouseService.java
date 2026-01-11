package ru.practicum.warehouse.service;

import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.contract.interactionapi.dto.warehouse.AddressDto;
import ru.practicum.contract.interactionapi.dto.warehouse.BookedProductsDto;
import ru.practicum.contract.interactionapi.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {

    void newProductInWarehouse(NewProductInWarehouseRequest newProduct);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto);

    void addProductToWarehouse(AddProductToWarehouseRequest product);

    AddressDto getWarehouseAddress();
}
