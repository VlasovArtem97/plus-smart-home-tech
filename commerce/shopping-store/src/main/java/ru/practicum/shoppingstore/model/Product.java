package ru.practicum.shoppingstore.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductCategory;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductState;
import ru.practicum.contract.interactionapi.dto.shoppingstore.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"productId"})
@Table(name = "products")
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_src", length = 1024)
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false, length = 15)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false, length = 15)
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false, length = 15)
    private ProductCategory productCategory;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
