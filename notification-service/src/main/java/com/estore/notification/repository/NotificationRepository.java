package com.estore.notification.repository;

import com.estore.notification.model.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {

    Flux<Notification> findByUserId(Long userId);

    Flux<Notification> findByStatus(String status);

    Flux<Notification> findByChannel(String channel);

    Flux<Notification> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}
