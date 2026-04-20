package com.n11bootcamp.paymentservice.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private BigDecimal amount;
    private String displayName;
    private LocalDateTime createdAt;
    private boolean success;
    private String message;

    public PaymentResponse(BigDecimal amount, String displayName, LocalDateTime createdAt, boolean success, String message) {
        this.amount = amount;
        this.displayName = displayName;
        this.createdAt = createdAt;
        this.success = success;
        this.message = message;
    }

    public BigDecimal getAmount() { return amount; }
    public String getDisplayName() { return displayName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
