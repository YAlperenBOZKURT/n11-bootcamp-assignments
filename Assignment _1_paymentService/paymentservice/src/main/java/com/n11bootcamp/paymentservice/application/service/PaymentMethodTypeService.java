package com.n11bootcamp.paymentservice.application.service;

import com.n11bootcamp.paymentservice.application.dto.PaymentMethodTypeResponse;
import com.n11bootcamp.paymentservice.domain.model.PaymentMethodType;

import java.util.List;

public interface PaymentMethodTypeService {
    List<PaymentMethodTypeResponse> getPaymentMethodTypes();
    PaymentMethodType getById(Long id);
}
