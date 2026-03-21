package com.estore.payment.service;

import com.estore.payment.dto.PaymentMethodRequest;
import com.estore.payment.dto.PaymentMethodResponse;
import com.estore.payment.model.PaymentMethod;
import com.estore.payment.repository.PaymentMethodRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public Flux<PaymentMethodResponse> findByUserId(Long userId) {
        return paymentMethodRepository.findByUserIdAndIsActiveTrue(userId)
                .map(this::toResponse);
    }

    public Mono<PaymentMethodResponse> findById(Long id) {
        return paymentMethodRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PaymentMethod", id)))
                .map(this::toResponse);
    }

    public Mono<PaymentMethodResponse> findDefaultByUserId(Long userId) {
        return paymentMethodRepository.findByUserIdAndIsDefaultTrueAndIsActiveTrue(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Default PaymentMethod for user", userId)))
                .map(this::toResponse);
    }

    public Mono<PaymentMethodResponse> create(PaymentMethodRequest request) {
        Instant now = Instant.now();
        PaymentMethod method = PaymentMethod.builder()
                .userId(request.userId())
                .methodType(request.methodType())
                .isDefault(request.isDefault() != null ? request.isDefault() : Boolean.FALSE)
                .gateway(request.gateway())
                .gatewayToken(request.gatewayToken())
                .gatewayCustomerId(request.gatewayCustomerId())
                .displayName(request.displayName())
                .cardLast4(request.cardLast4())
                .cardBrand(request.cardBrand())
                .cardExpMonth(request.cardExpMonth())
                .cardExpYear(request.cardExpYear())
                .billingAddress(request.billingAddress())
                .isActive(Boolean.TRUE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return paymentMethodRepository.save(method)
                .map(this::toResponse);
    }

    public Mono<PaymentMethodResponse> update(Long id, PaymentMethodRequest request) {
        return paymentMethodRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PaymentMethod", id)))
                .flatMap(method -> {
                    method.setMethodType(request.methodType());
                    method.setGateway(request.gateway());
                    method.setGatewayToken(request.gatewayToken());
                    method.setGatewayCustomerId(request.gatewayCustomerId());
                    method.setDisplayName(request.displayName());
                    method.setCardLast4(request.cardLast4());
                    method.setCardBrand(request.cardBrand());
                    method.setCardExpMonth(request.cardExpMonth());
                    method.setCardExpYear(request.cardExpYear());
                    method.setBillingAddress(request.billingAddress());
                    if (request.isDefault() != null) {
                        method.setIsDefault(request.isDefault());
                    }
                    method.setUpdatedAt(Instant.now());
                    return paymentMethodRepository.save(method);
                })
                .map(this::toResponse);
    }

    public Mono<Void> deleteById(Long id) {
        return paymentMethodRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PaymentMethod", id)))
                .flatMap(method -> {
                    method.setIsActive(Boolean.FALSE);
                    method.setUpdatedAt(Instant.now());
                    return paymentMethodRepository.save(method);
                })
                .then();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private PaymentMethodResponse toResponse(PaymentMethod m) {
        return new PaymentMethodResponse(
                m.getId(),
                m.getUserId(),
                m.getMethodType(),
                m.getIsDefault(),
                m.getGateway(),
                m.getGatewayCustomerId(),
                m.getDisplayName(),
                m.getCardLast4(),
                m.getCardBrand(),
                m.getCardExpMonth(),
                m.getCardExpYear(),
                m.getBillingAddress(),
                m.getIsActive(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}
