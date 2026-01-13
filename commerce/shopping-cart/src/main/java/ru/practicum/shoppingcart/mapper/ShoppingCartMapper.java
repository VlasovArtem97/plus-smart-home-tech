package ru.practicum.shoppingcart.mapper;

import org.mapstruct.Mapper;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.shoppingcart.model.ShoppingCart;

import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    ShoppingCartDto toShoppingCartDtoFromShoppingCart(ShoppingCart shoppingCart);

    default ShoppingCartDto toUpdateQuantity(ShoppingCartDto shoppingCartDto, Map<UUID, Long> products) {
        Map<UUID, Long> cartProducts = shoppingCartDto.getProducts();

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID id = entry.getKey();
            Long quantityProduct = entry.getValue();

            cartProducts.compute(id, (k, currentQuantity) ->
                    (currentQuantity == null ? 0 : currentQuantity) + quantityProduct);
        }
        return shoppingCartDto;
    }
}
