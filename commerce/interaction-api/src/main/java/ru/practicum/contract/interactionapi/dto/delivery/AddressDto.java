package ru.practicum.contract.interactionapi.dto.delivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    @NotBlank
    @Size(min = 3, max = 255)
    private String country;

    @NotBlank
    @Size(min = 3, max = 255)
    private String city;

    @NotBlank
    @Size(min = 3, max = 255)
    private String street;

    @NotBlank
    @Size(min = 3, max = 255)
    private String house;

    @NotBlank
    @Size(min = 3, max = 255)
    private String flat;
}
