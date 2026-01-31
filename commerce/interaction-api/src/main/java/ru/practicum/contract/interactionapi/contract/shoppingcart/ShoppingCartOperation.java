package ru.practicum.contract.interactionapi.contract.shoppingcart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.util.CheckNegativeValueInMapProduct;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
public interface ShoppingCartOperation {

    @GetMapping()
    ShoppingCartDto getShoppingCart(@RequestParam("username") String userName);

    @PutMapping()
    ShoppingCartDto addProductToShoppingCart(@Size(min = 1) @NotNull @CheckNegativeValueInMapProduct
                                             @RequestBody Map<UUID, Long> products,
                                             @NotBlank @RequestParam("username") String userName);

    @DeleteMapping
    void deactivateCurrentShoppingCart(@NotBlank @RequestParam("username") String userName);

    @PostMapping("/remove")
    ShoppingCartDto removeFromShoppingCart(@NotBlank @RequestParam("username") String userName,
                                           @Size(min = 1) @NotNull @RequestBody List<UUID> productId);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@NotBlank @RequestParam("username") String userName,
                                          @Valid @RequestBody ChangeProductQuantityRequest quantityRequest);
}
