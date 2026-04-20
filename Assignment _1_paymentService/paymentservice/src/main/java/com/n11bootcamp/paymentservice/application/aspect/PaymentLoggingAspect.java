package com.n11bootcamp.paymentservice.application.aspect;

import com.n11bootcamp.paymentservice.application.dto.PaymentResponse;
import com.n11bootcamp.paymentservice.domain.model.PaymentMethodType;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Aspect
@Component
public class PaymentLoggingAspect {

    @Before("execution(* com.n11bootcamp.paymentservice.application.service.PaymentService+.processPayment(..)) && args(amount, type)")
    public void beforePayment(BigDecimal amount, PaymentMethodType type) {
        System.out.println(type.getDisplayName() + " yöntemiyle ödeme işlemi başlatıldı (Tutar: " + amount + " TL)");
    }

    @AfterReturning(
            pointcut = "execution(* com.n11bootcamp.paymentservice.application.service.PaymentService+.processPayment(..))",
            returning = "response")
    public void afterPayment(PaymentResponse response) {
        System.out.println("Ödeme başarılı → Tutar: " + response.getAmount()
                + " TL, Zaman: " + response.getCreatedAt()
                + ", Yöntem: " + response.getDisplayName());
    }
}
