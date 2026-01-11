package ru.practicum.shoppingcart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.contract.interactionapi.contract.shoppingcart.ShoppingCartOperation;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.shoppingcart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartOperation {

    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCart(String userName) {
        return shoppingCartService.getShoppingCart(userName);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(Map<UUID, Long> products, String userName) {
        return shoppingCartService.addProductToShoppingCart(products, userName);
    }

    @Override
    public void deactivateCurrentShoppingCart(String userName) {
        shoppingCartService.deactivateCurrentShoppingCart(userName);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String userName, List<UUID> productId) {
        return shoppingCartService.removeFromShoppingCart(userName, productId);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String userName, ChangeProductQuantityRequest quantityRequest) {
        return shoppingCartService.changeProductQuantity(userName, quantityRequest);
    }
}
