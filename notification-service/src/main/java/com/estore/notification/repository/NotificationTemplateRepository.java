package com.estore.notification.repository;

import com.estore.notification.model.NotificationTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationTemplateRepository extends ReactiveCrudRepository<NotificationTemplate, Long> {

    Mono<NotificationTemplate> findByName(String name);

    Flux<NotificationTemplate> findByChannel(String channel);

    Flux<NotificationTemplate> findByIsActiveTrue();
}
