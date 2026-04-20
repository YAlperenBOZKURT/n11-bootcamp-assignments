package com.n11bootcamp.paymentservice.application.service.impl;

import com.n11bootcamp.paymentservice.application.dto.PaymentMethodTypeResponse;
import com.n11bootcamp.paymentservice.application.service.PaymentMethodTypeService;
import com.n11bootcamp.paymentservice.domain.exception.PaymentMethodNotFoundException;
import com.n11bootcamp.paymentservice.domain.model.PaymentMethodType;
import com.n11bootcamp.paymentservice.infrastructure.repository.PaymentMethodTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodTypeServiceImpl implements PaymentMethodTypeService {

    private final PaymentMethodTypeRepository paymentMethodTypeRepository;

    public PaymentMethodTypeServiceImpl(PaymentMethodTypeRepository paymentMethodTypeRepository) {
        this.paymentMethodTypeRepository = paymentMethodTypeRepository;
    }

    @Override
    public List<PaymentMethodTypeResponse> getPaymentMethodTypes() {
        return paymentMethodTypeRepository.findAll()
                .stream()
                .map(type -> new PaymentMethodTypeResponse(type.getId(), type.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public PaymentMethodType getById(Long id) {
        return paymentMethodTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentMethodNotFoundException(id));
    }
}