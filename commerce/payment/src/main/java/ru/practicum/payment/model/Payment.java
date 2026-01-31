package ru.practicum.payment.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.contract.interactionapi.dto.payment.PaymentStates;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode(of = "paymentId")
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "total_payment", precision = 12, scale = 2)
    private BigDecimal totalPayment;

    @Column(name = "delivery_total", precision = 12, scale = 2)
    private BigDecimal deliveryTotal;

    @Column(name = "fee_total", precision = 12, scale = 2)
    private BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_state", nullable = false)
    private PaymentStates paymentState;

    @Column(name = "product_price", precision = 12, scale = 2)
    private BigDecimal productPrice;
}
