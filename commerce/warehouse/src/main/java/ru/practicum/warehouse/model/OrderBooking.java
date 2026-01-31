package ru.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@ToString
@Builder
@EqualsAndHashCode(of = "orderId")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders_booking")
public class OrderBooking {

    @Id
    @Column(name = "order_id")
    private UUID orderId;

    @ElementCollection
    @CollectionTable(
            name = "orders_booking_products",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity", nullable = false)
    private Map<UUID, Long> products;

    @Column(name = "delivery_id")
    private UUID deliveryId;
}
