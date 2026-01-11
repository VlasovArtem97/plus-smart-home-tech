package ru.practicum.contract.interactionapi.contract.shoppingstore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductDto;

import java.util.UUID;

@Validated
public interface ShoppingStoreOperation {

    @GetMapping
    Page<ProductDto> getProducts(@NotBlank @RequestParam String category,
                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                 @Positive @RequestParam(defaultValue = "1") int size,
                                 @RequestParam(required = false) String sort);

    @PutMapping
    ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody UUID id);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@RequestParam UUID productId, @RequestParam String quantityState);

    @GetMapping("/{productId}")
    ProductDto getProduct(@NotNull @PathVariable UUID productId);
}
