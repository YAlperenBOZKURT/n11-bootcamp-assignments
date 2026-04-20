package com.n11bootcamp.paymentservice.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;


// this annotation ensures that null fields are not included in the JSON response
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String errorMessage;
    private LocalDateTime timestamp;

    private ApiResponse(boolean success, T data, String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
        this.timestamp = LocalDateTime.now();
    }

    // T is the type of the data or list being returned in case of success 
    public static <T> ApiResponse<T> success(T data) { 
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(String message) { 
        return new ApiResponse<>(false, null, message);
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
