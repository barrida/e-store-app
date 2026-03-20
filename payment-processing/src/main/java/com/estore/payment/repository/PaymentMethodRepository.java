package com.estore.payment.repository;

import com.estore.payment.model.PaymentMethod;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentMethodRepository extends ReactiveCrudRepository<PaymentMethod, Long> {

    Flux<PaymentMethod> findByUserId(Long userId);

    Mono<PaymentMethod> findByUserIdAndIsDefaultTrueAndIsActiveTrue(Long userId);

    Flux<PaymentMethod> findByUserIdAndIsActiveTrue(Long userId);
}
