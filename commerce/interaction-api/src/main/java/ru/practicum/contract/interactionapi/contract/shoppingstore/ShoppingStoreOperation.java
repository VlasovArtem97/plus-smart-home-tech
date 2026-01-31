package ru.practicum.contract.interactionapi.contract.shoppingstore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductDto;

import java.util.List;
import java.util.Set;
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
    boolean removeProductFromStore(@NotNull @RequestBody UUID id);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@NotNull @RequestParam UUID productId, @NotNull @RequestParam String quantityState);

    @GetMapping("/{productId}")
    ProductDto getProduct(@NotNull @PathVariable UUID productId);

    //Добавил отдельный метод для получения списка продуктов для получения цены одним запросом
    @GetMapping("/checkPrice")
    List<ProductDto> getProducts(@NotNull @Size(min = 1) @RequestParam Set<UUID> products);
}
