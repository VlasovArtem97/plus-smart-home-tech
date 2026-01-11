package ru.practicum.shoppingcart.mapper;

import org.mapstruct.Mapper;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.shoppingcart.model.ShoppingCart;

import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    ShoppingCartDto toShoppingCartDtoFromShoppingCart(ShoppingCart shoppingCart);

    //    ShoppingCart toShoppingCartFromShoppingCartDto(ShoppingCartDto shoppingCartDto);
//
    default ShoppingCartDto toUpdateQuantity(ShoppingCartDto shoppingCartDto, Map<UUID, Long> products) {
        for (Map.Entry<UUID, Long> sc : shoppingCartDto.getProducts().entrySet()) {
            UUID id = sc.getKey();
            Long quantity = sc.getValue();
            Long plusQuantity = products.getOrDefault(id, 0L);
            shoppingCartDto.getProducts().put(id, quantity + plusQuantity);
        }
        return shoppingCartDto;
    }
}
