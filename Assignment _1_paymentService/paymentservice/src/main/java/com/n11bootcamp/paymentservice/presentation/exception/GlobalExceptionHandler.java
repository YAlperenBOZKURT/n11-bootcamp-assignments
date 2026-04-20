package com.n11bootcamp.paymentservice.presentation.exception;

import com.n11bootcamp.paymentservice.domain.exception.PaymentMethodNotFoundException;
import com.n11bootcamp.paymentservice.presentation.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // for PaymentMethodNotFoundException 
    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentMethodNotFound(PaymentMethodNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // for any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Beklenmeyen bir hata oluştu."));
    }
}
