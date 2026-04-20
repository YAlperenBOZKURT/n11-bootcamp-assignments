package com.n11bootcamp.paymentservice.application.dto;

public class PaymentMethodTypeResponse {
    private Long id;
    private String displayName;

    public PaymentMethodTypeResponse(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Long getId() { return id; }
    public String getDisplayName() { return displayName; }
}

