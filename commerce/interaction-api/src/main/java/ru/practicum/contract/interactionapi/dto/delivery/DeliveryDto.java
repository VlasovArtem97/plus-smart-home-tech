package ru.practicum.contract.interactionapi.dto.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    @Null
    private UUID deliveryId;

    @NotNull
    @Valid
    private AddressDto fromAddress;

    @NotNull
    @Valid
    private AddressDto toAddress;

    @NotNull
    private UUID orderId;

    @Null
    private DeliveryState deliveryState;

    /*Решил добавить 3 поля, чтобы можно было сразу передать для создания доставки в delivery, а не обращаться еще раз
    к складу. Поскольку в ТЗ требуется хранения в БД этих полей
     */

//    @DecimalMin("1.0")
//    private double deliveryVolume;
//
//    @DecimalMin("1.0")
//    private double deliveryWeight;
//
//    @NotNull
//    private Boolean fragile;


}
