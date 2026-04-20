package com.n11bootcamp.paymentservice.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "payment_method_type_id")
    private PaymentMethodType paymentMethodType;

    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(BigDecimal amount, PaymentMethodType paymentMethodType) {
        this.amount = amount;
        this.paymentMethodType = paymentMethodType;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethodType getPaymentMethodType() {
        return paymentMethodType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


}
