package com.estore.user.repository;

import com.estore.user.model.UserProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, Long> {

    Mono<UserProfile> findByUserId(Long userId);
}
