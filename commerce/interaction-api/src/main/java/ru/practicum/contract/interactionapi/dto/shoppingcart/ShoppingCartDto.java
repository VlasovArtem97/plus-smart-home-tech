package ru.practicum.contract.interactionapi.dto.shoppingcart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.contract.interactionapi.util.CheckNegativeValueInMapProduct;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {

    @NotNull
    private UUID shoppingCartId;

    //добавлено поле с именем
    @NotBlank
    @Size(min = 1, max = 255)
    private String userName;

    @NotNull
    @Size(min = 1)
    @CheckNegativeValueInMapProduct
    private Map<UUID, Long> products;
}
