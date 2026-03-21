package com.estore.payment.repository;

import com.estore.payment.model.PaymentTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentTransactionRepository extends ReactiveCrudRepository<PaymentTransaction, Long> {

    Flux<PaymentTransaction> findByOrderId(Long orderId);

    Flux<PaymentTransaction> findByUserId(Long userId);

    Mono<PaymentTransaction> findByTransactionReference(String transactionReference);

    Flux<PaymentTransaction> findByStatus(String status);
}
