package com.n11bootcamp.paymentservice.infrastructure.repository;

import com.n11bootcamp.paymentservice.domain.model.PaymentMethodType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodTypeRepository extends JpaRepository<PaymentMethodType, Long> {
}
