package com.n11bootcamp.paymentservice.application.service;

import com.n11bootcamp.paymentservice.application.dto.PaymentResponse;
import com.n11bootcamp.paymentservice.domain.model.PaymentMethodType;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentResponse processPayment(BigDecimal amount, PaymentMethodType paymentMethodType);
    String getCode();
}
