package ru.practicum.shoppingcart.service;

import ru.practicum.contract.interactionapi.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(String userName);

    ShoppingCartDto addProductToShoppingCart(Map<UUID, Long> products, String userName);

    void deactivateCurrentShoppingCart(String userName);

    ShoppingCartDto removeFromShoppingCart(String userName, List<UUID> productId);

    ShoppingCartDto changeProductQuantity(String userName, ChangeProductQuantityRequest quantityRequest);
}
