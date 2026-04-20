package com.n11bootcamp.paymentservice.domain.exception;

public class PaymentMethodNotFoundException extends RuntimeException {


    // For service
    public PaymentMethodNotFoundException(Long id) {
        super("Ödeme yöntemi bulunamadı. ID: " + id);
    }

    // For factory
    public PaymentMethodNotFoundException(String code) {
        super("Geçersiz ödeme yöntemi: " + code);
    }
}
