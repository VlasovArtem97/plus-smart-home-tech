package ru.practicum.contract.interactionapi.dto.warehouse;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssemblyProductsForOrderRequest {

    @NotNull
    @Size(min = 1)
    @CheckNegativeValueInMapProduct
    private Map<UUID, Long> products;

    @NotNull
    private UUID orderId;
}
