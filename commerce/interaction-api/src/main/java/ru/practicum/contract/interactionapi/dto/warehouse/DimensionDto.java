package ru.practicum.contract.interactionapi.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {

    @DecimalMin("1.0")
    private double depth;

    @DecimalMin("1.0")
    private double height;

    @DecimalMin("1.0")
    private double width;
}
