package ru.practicum.contract.interactionapi.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private Boolean fragile;

    @NotNull
    @Valid
    private DimensionDto dimension;

    @DecimalMin("1.0")
    private double weight;
}
