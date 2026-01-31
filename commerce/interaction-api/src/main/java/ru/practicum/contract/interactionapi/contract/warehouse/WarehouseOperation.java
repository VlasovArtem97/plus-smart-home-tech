package ru.practicum.contract.interactionapi.contract.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.interactionapi.dto.delivery.AddressDto;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.dto.warehouse.*;
import ru.practicum.contract.interactionapi.util.CheckNegativeValueInMapProduct;

import java.util.Map;
import java.util.UUID;

@Validated
public interface WarehouseOperation {

    @PutMapping
    void newProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest newProduct);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@Valid @RequestBody ShoppingCartDto cartDto);

    @PostMapping("/add")
    void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest product);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@Valid @NotNull @RequestBody AssemblyProductsForOrderRequest orderRequest);

    /*добавил UUID orderId для того, чтобы можно было уменьшить в таблице забронированных товаров.
    Пока не знаю насколько это нужно. Но можно, допустим вернуть часть товаров
     */
    @PostMapping("/return/{orderId}")
    void acceptReturn(@NotNull @Size(min = 1) @CheckNegativeValueInMapProduct @RequestBody Map<UUID, Long> products,
                      @PathVariable @NotNull UUID orderId);

    @PostMapping("/shipped")
    void shippedToDelivery(@Valid @NotNull @RequestBody ShippedToDeliveryRequest shippedToDeliveryRequest);

}
