package ru.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"productId"})
@Table(name = " warehouse_products")
@ToString
public class WarehouseProduct {

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(nullable = false)
    private Boolean fragile;

    @Embedded
    private Dimension dimension;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    @Builder.Default
    private Long quantity = 0L;
}
