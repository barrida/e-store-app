package com.estore.payment.controller;

import com.estore.payment.dto.PaymentTransactionRequest;
import com.estore.payment.dto.PaymentTransactionResponse;
import com.estore.payment.service.PaymentTransactionService;
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
public class PaymentController {

    private final PaymentTransactionService transactionService;

    @GetMapping("/api/payments")
    public Mono<ApiResponse<List<PaymentTransactionResponse>>> getPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long orderId) {
        if (orderId != null) {
            return transactionService.findByOrderId(orderId)
                    .collectList()
                    .map(ApiResponse::ok);
        }
        if (userId != null) {
            return transactionService.findByUserId(userId)
                    .collectList()
                    .map(ApiResponse::ok);
        }
        return transactionService.findAll()
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/payments/{id}")
    public Mono<ApiResponse<PaymentTransactionResponse>> getPaymentById(@PathVariable Long id) {
        return transactionService.findById(id)
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/orders/{orderId}/payments")
    public Mono<ApiResponse<List<PaymentTransactionResponse>>> getPaymentsByOrder(
            @PathVariable Long orderId) {
        return transactionService.findByOrderId(orderId)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/users/{userId}/payments")
    public Mono<ApiResponse<List<PaymentTransactionResponse>>> getPaymentsByUser(
            @PathVariable Long userId) {
        return transactionService.findByUserId(userId)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PostMapping("/api/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<PaymentTransactionResponse>> initiatePayment(
            @Valid @RequestBody PaymentTransactionRequest request) {
        return transactionService.initiateTransaction(request)
                .map(tx -> ApiResponse.ok("Payment transaction initiated", tx));
    }

    @PutMapping("/api/payments/{id}/status")
    public Mono<ApiResponse<PaymentTransactionResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return transactionService.updateStatus(id, status)
                .map(tx -> ApiResponse.ok("Payment status updated", tx));
    }

    @DeleteMapping("/api/payments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePayment(@PathVariable Long id) {
        return transactionService.deleteById(id);
    }
}
