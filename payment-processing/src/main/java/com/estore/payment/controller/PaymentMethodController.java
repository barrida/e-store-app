package com.estore.payment.controller;

import com.estore.payment.dto.PaymentMethodRequest;
import com.estore.payment.dto.PaymentMethodResponse;
import com.estore.payment.service.PaymentMethodService;
import com.estore.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping("/api/users/{userId}/payment-methods")
    public Mono<ApiResponse<List<PaymentMethodResponse>>> getPaymentMethodsByUser(
            @PathVariable Long userId) {
        return paymentMethodService.findByUserId(userId)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/payment-methods/{id}")
    public Mono<ApiResponse<PaymentMethodResponse>> getPaymentMethodById(@PathVariable Long id) {
        return paymentMethodService.findById(id)
                .map(ApiResponse::ok);
    }

    @PostMapping("/api/payment-methods")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<PaymentMethodResponse>> createPaymentMethod(
            @Valid @RequestBody PaymentMethodRequest request) {
        return paymentMethodService.create(request)
                .map(method -> ApiResponse.ok("Payment method created", method));
    }

    @PutMapping("/api/payment-methods/{id}")
    public Mono<ApiResponse<PaymentMethodResponse>> updatePaymentMethod(
            @PathVariable Long id,
            @Valid @RequestBody PaymentMethodRequest request) {
        return paymentMethodService.update(id, request)
                .map(method -> ApiResponse.ok("Payment method updated", method));
    }

    @DeleteMapping("/api/payment-methods/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePaymentMethod(@PathVariable Long id) {
        return paymentMethodService.deleteById(id);
    }
}
