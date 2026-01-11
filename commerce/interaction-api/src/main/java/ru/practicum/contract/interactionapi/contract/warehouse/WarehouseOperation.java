package ru.practicum.contract.interactionapi.contract.warehouse;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.contract.interactionapi.dto.warehouse.AddressDto;
import ru.practicum.contract.interactionapi.dto.warehouse.BookedProductsDto;
import ru.practicum.contract.interactionapi.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseOperation {

    @PutMapping
    void newProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest newProduct);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@Valid @RequestBody ShoppingCartDto cartDto);

    @PostMapping("/add")
    void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest product);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
