package ru.practicum.shoppingstore.service;

import org.springframework.data.domain.Page;
import ru.practicum.contract.interactionapi.dto.shoppingstore.Pageable;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductCategory;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductDto;
import ru.practicum.contract.interactionapi.dto.shoppingstore.SetProductQuantityStateRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ShoppingStoreService {

    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean removeProductFromStore(UUID id);

    boolean setProductQuantityState(SetProductQuantityStateRequest request);

    ProductDto getProduct(UUID productId);

    List<ProductDto> getProducts(Set<UUID> products);
}
