package com.n11bootcamp.paymentservice.presentation.controller;

import com.n11bootcamp.paymentservice.application.dto.PaymentMethodTypeResponse;
import com.n11bootcamp.paymentservice.application.dto.PaymentResponse;
import com.n11bootcamp.paymentservice.application.factory.PaymentServiceFactory;
import com.n11bootcamp.paymentservice.application.service.PaymentMethodTypeService;
import com.n11bootcamp.paymentservice.domain.model.PaymentMethodType;
import com.n11bootcamp.paymentservice.presentation.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentServiceFactory paymentServiceFactory;
    private final PaymentMethodTypeService paymentMethodTypeService;

    public PaymentController(PaymentServiceFactory paymentServiceFactory,
                             PaymentMethodTypeService paymentMethodTypeService) {
        this.paymentServiceFactory = paymentServiceFactory;
        this.paymentMethodTypeService = paymentMethodTypeService;
    }

    @GetMapping("/method-types")
    public ApiResponse<List<PaymentMethodTypeResponse>> getMethodTypes() {
        List<PaymentMethodTypeResponse> methodTypes = paymentMethodTypeService.getPaymentMethodTypes();
        return ApiResponse.success(methodTypes);
    }

    @PostMapping("/pay")
    public ApiResponse<PaymentResponse> pay(@RequestParam BigDecimal amount,
                                            @RequestParam Long paymentMethodTypeId) {
        PaymentMethodType type = paymentMethodTypeService.getById(paymentMethodTypeId);
        PaymentResponse response = paymentServiceFactory.getService(type.getCode()).processPayment(amount, type);
        return ApiResponse.success(response);
    }
}
