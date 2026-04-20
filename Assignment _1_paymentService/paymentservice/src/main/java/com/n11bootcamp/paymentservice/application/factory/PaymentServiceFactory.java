package com.n11bootcamp.paymentservice.application.factory;

import com.n11bootcamp.paymentservice.application.service.PaymentService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentServiceFactory {

    private final Map<String, PaymentService> services;

    public PaymentServiceFactory(List<PaymentService> serviceList) {
        this.services = serviceList.stream()
                .collect(Collectors.toMap(PaymentService::getCode, s -> s));
    }

    public PaymentService getService(String code) {
        PaymentService service = services.get(code);
        if (service == null) {
            throw new RuntimeException("Geçersiz ödeme yöntemi: " + code);
        }
        return service;
    }
}