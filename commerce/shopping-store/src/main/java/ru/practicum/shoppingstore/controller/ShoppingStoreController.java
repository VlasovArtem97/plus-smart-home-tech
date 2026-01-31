package ru.practicum.shoppingstore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.contract.interactionapi.contract.shoppingstore.ShoppingStoreOperation;
import ru.practicum.contract.interactionapi.dto.shoppingstore.*;
import ru.practicum.shoppingstore.service.ShoppingStoreService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreOperation {

    private final ShoppingStoreService shoppingStore;

    @Override
    public Page<ProductDto> getProducts(String category, int from, int size, String sort) {
        ProductCategory productCategory = ProductCategory.toCategoryFromString(category);
        return shoppingStore.getProducts(productCategory,
                Pageable.builder()
                        .from(from)
                        .size(size)
                        .sort(sort)
                        .build());
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        return shoppingStore.createNewProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        return shoppingStore.updateProduct(productDto);
    }

    @Override
    public boolean removeProductFromStore(UUID id) {
        return shoppingStore.removeProductFromStore(id);
    }

    @Override
    public boolean setProductQuantityState(UUID productId, String quantityState) {
        QuantityState state = QuantityState.toQuantityStateFromString(quantityState);
        return shoppingStore.setProductQuantityState(SetProductQuantityStateRequest.builder()
                .productId(productId)
                .quantityState(state)
                .build());
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return shoppingStore.getProduct(productId);
    }

    @Override
    public List<ProductDto> getProducts(Set<UUID> products) {
        return shoppingStore.getProducts(products);
    }
}
