package com.estore.payment.service;

import com.estore.payment.dto.PaymentTransactionRequest;
import com.estore.payment.dto.PaymentTransactionResponse;
import com.estore.payment.model.PaymentStatus;
import com.estore.payment.model.PaymentTransaction;
import com.estore.payment.model.PaymentType;
import com.estore.payment.repository.PaymentTransactionRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private final PaymentTransactionRepository transactionRepository;

    public Flux<PaymentTransactionResponse> findAll() {
        return transactionRepository.findAll()
                .map(this::toResponse);
    }

    public Flux<PaymentTransactionResponse> findByOrderId(Long orderId) {
        return transactionRepository.findByOrderId(orderId)
                .map(this::toResponse);
    }

    public Flux<PaymentTransactionResponse> findByUserId(Long userId) {
        return transactionRepository.findByUserId(userId)
                .map(this::toResponse);
    }

    public Flux<PaymentTransactionResponse> findByStatus(String status) {
        return transactionRepository.findByStatus(status)
                .map(this::toResponse);
    }

    public Mono<PaymentTransactionResponse> findById(Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PaymentTransaction", id)))
                .map(this::toResponse);
    }

    public Mono<PaymentTransactionResponse> findByTransactionReference(String reference) {
        return transactionRepository.findByTransactionReference(reference)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PaymentTransaction", reference)))
                .map(this::toResponse);
    }

    public Mono<PaymentTransactionResponse> initiateTransaction(PaymentTransactionRequest request) {
        Instant now = Instant.now();
        String paymentType = request.paymentType() != null
                ? request.paymentType()
                : PaymentType.CHARGE.toDbValue();

        PaymentTransaction transaction = PaymentTransaction.builder()
                .transactionReference(generateTransactionReference())
                .orderId(request.orderId())
                .userId(request.userId())
                .paymentMethodId(request.paymentMethodId())
                .paymentType(paymentType)
                .status(PaymentStatus.PENDING.toDbValue())
                .amount(request.amount())
                .currency(request.currency() != null ? request.currency() : "USD")
                .gateway(request.gateway())
                .refundedAmount(BigDecimal.ZERO)
                .ipAddress(request.ipAddress())
                .userAgent(request.userAgent())
                .metadata(request.metadata())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return transactionRepository.save(transaction)
                .map(this::toResponse);
    }

    public Mono<PaymentTransactionResponse> updateStatus(Long id, String status) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PaymentTransaction", id)))
                .flatMap(transaction -> {
                    Instant now = Instant.now();
                    transaction.setStatus(status);
                    transaction.setUpdatedAt(now);
                    applyStatusTimestamp(transaction, status, now);
                    return transactionRepository.save(transaction);
                })
                .map(this::toResponse);
    }

    public Mono<PaymentTransactionResponse> update(Long id, PaymentTransactionRequest request) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PaymentTransaction", id)))
                .flatMap(transaction -> {
                    transaction.setOrderId(request.orderId());
                    transaction.setUserId(request.userId());
                    transaction.setPaymentMethodId(request.paymentMethodId());
                    if (request.paymentType() != null) {
                        transaction.setPaymentType(request.paymentType());
                    }
                    transaction.setAmount(request.amount());
                    if (request.currency() != null) {
                        transaction.setCurrency(request.currency());
                    }
                    transaction.setGateway(request.gateway());
                    transaction.setIpAddress(request.ipAddress());
                    transaction.setUserAgent(request.userAgent());
                    transaction.setMetadata(request.metadata());
                    transaction.setUpdatedAt(Instant.now());
                    return transactionRepository.save(transaction);
                })
                .map(this::toResponse);
    }

    public Mono<Void> deleteById(Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PaymentTransaction", id)))
                .flatMap(transactionRepository::delete);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private void applyStatusTimestamp(PaymentTransaction t, String status, Instant now) {
        switch (status.toLowerCase()) {
            case "authorized"          -> t.setAuthorizedAt(now);
            case "captured"            -> t.setCapturedAt(now);
            case "failed"              -> t.setFailedAt(now);
            case "refunded",
                 "partially_refunded"  -> t.setRefundedAt(now);
            default                    -> { /* no extra timestamp */ }
        }
    }

    private PaymentTransactionResponse toResponse(PaymentTransaction t) {
        return new PaymentTransactionResponse(
                t.getId(),
                t.getTransactionReference(),
                t.getOrderId(),
                t.getUserId(),
                t.getPaymentMethodId(),
                t.getPaymentType(),
                t.getStatus(),
                t.getAmount(),
                t.getCurrency(),
                t.getGateway(),
                t.getGatewayTransactionId(),
                t.getGatewayResponse(),
                t.getAuthorizedAt(),
                t.getCapturedAt(),
                t.getFailedAt(),
                t.getRefundedAt(),
                t.getFailureReason(),
                t.getRefundedAmount(),
                t.getParentTransactionId(),
                t.getIpAddress(),
                t.getUserAgent(),
                t.getMetadata(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
