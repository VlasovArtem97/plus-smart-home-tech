package ru.practicum.contract.interactionapi.dto.order;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReturnRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    @CheckNegativeValueInMapProduct
    @Size(min = 1)
    private Map<UUID, Long> products;
}
