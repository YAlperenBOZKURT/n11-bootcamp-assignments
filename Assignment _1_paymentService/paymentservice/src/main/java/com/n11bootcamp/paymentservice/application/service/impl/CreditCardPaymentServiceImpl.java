package com.n11bootcamp.paymentservice.application.service.impl;

import com.n11bootcamp.paymentservice.application.dto.PaymentResponse;
import com.n11bootcamp.paymentservice.application.service.PaymentService;
import com.n11bootcamp.paymentservice.domain.model.Payment;
import com.n11bootcamp.paymentservice.domain.model.PaymentMethodType;
import com.n11bootcamp.paymentservice.infrastructure.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreditCardPaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public CreditCardPaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String getCode() {
        return "CREDIT_CARD";
    }

    @Override
    public PaymentResponse processPayment(BigDecimal amount, PaymentMethodType paymentMethodType) {
        Payment payment = new Payment(amount, paymentMethodType);
        paymentRepository.save(payment);

        return new PaymentResponse(
                payment.getAmount(),
                paymentMethodType.getDisplayName(),
                payment.getCreatedAt(),
                true,
                "Kredi kartı ile ödeme başarıyla tamamlandı"
        );
    }
}