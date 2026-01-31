package ru.practicum.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.payment.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(UUID orderId);

    Optional<Payment> findByPaymentId(UUID paymentId);
}
