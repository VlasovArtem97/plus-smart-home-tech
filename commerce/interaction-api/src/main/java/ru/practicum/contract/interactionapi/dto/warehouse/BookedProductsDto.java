package ru.practicum.contract.interactionapi.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductsDto {

    private double deliveryVolume;

    private double deliveryWeight;

    private Boolean fragile;
}
