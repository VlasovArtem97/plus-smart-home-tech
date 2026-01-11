package ru.practicum.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.contract.interactionapi.contract.warehouse.WarehouseOperation;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.contract.interactionapi.dto.warehouse.AddressDto;
import ru.practicum.contract.interactionapi.dto.warehouse.BookedProductsDto;
import ru.practicum.contract.interactionapi.dto.warehouse.NewProductInWarehouseRequest;
import ru.practicum.warehouse.service.WarehouseService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/warehouse")
public class WarehouseController implements WarehouseOperation {

    private final WarehouseService warehouseService;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest newProduct) {
        warehouseService.newProductInWarehouse(newProduct);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto) {
        return warehouseService.checkProductQuantityEnoughForShoppingCart(cartDto);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest product) {
        warehouseService.addProductToWarehouse(product);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}
