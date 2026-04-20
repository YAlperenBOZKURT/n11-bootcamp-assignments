package com.n11bootcamp.paymentservice.domain.model;


import jakarta.persistence.*;

@Entity
@Table(name = "payment_method_types")
public class PaymentMethodType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String displayName;

    public PaymentMethodType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public PaymentMethodType() {}

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
