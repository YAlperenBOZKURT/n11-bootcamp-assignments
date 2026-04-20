package com.n11bootcamp.paymentservice.application.service.impl;

import com.n11bootcamp.paymentservice.application.dto.PaymentResponse;
import com.n11bootcamp.paymentservice.application.service.PaymentService;
import com.n11bootcamp.paymentservice.domain.model.Payment;
import com.n11bootcamp.paymentservice.domain.model.PaymentMethodType;
import com.n11bootcamp.paymentservice.infrastructure.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaypalPaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaypalPaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String getCode() {
        return "PAYPAL";
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
                "PayPal ile ödeme başarıyla tamamlandı"
        );
    }
}