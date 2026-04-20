package com.n11bootcamp.paymentservice.infrastructure.repository;

import com.n11bootcamp.paymentservice.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
