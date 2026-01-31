package ru.practicum.contract.interactionapi.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.contract.interactionapi.util.CheckNegativeValueInMapProduct;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID shoppingCartId;

    @NotBlank
    private String userName;

    @NotNull
    @Size(min = 1)
    @CheckNegativeValueInMapProduct
    private Map<UUID, Long> products;

    private UUID paymentId;

    private UUID deliveryId;

    @NotNull
    private StateOrderDto state;

    private Double deliveryWeight;

    private Double deliveryVolume;

    private Boolean fragile;

    private BigDecimal totalPrice;

    private BigDecimal deliveryPrice;

    private BigDecimal productPrice;

    //добавил налог, для того, чтобы пользователь понимал, почему изменилась стоимость товара
    private BigDecimal feeTotal;

}
