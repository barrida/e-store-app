package com.estore.notification.repository;

import com.estore.notification.model.UserNotificationPreference;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface UserNotificationPreferenceRepository extends ReactiveCrudRepository<UserNotificationPreference, Long> {

    Flux<UserNotificationPreference> findByUserId(Long userId);

    Flux<UserNotificationPreference> findByUserIdAndChannel(Long userId, String channel);
}
