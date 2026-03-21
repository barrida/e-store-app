package com.estore.notification.service;

import com.estore.notification.dto.UserPreferenceRequest;
import com.estore.notification.dto.UserPreferenceResponse;
import com.estore.notification.model.UserNotificationPreference;
import com.estore.notification.repository.UserNotificationPreferenceRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserNotificationPreferenceService {

    private final UserNotificationPreferenceRepository preferenceRepository;

    public Flux<UserPreferenceResponse> findByUserId(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .map(this::toResponse);
    }

    public Flux<UserPreferenceResponse> findByUserIdAndChannel(Long userId, String channel) {
        return preferenceRepository.findByUserIdAndChannel(userId, channel)
                .map(this::toResponse);
    }

    public Mono<UserPreferenceResponse> findById(Long id) {
        return preferenceRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("UserNotificationPreference", id)))
                .map(this::toResponse);
    }

    public Mono<UserPreferenceResponse> create(UserPreferenceRequest request) {
        Instant now = Instant.now();
        UserNotificationPreference preference = UserNotificationPreference.builder()
                .userId(request.userId())
                .channel(request.channel())
                .templateName(request.templateName())
                .isEnabled(request.isEnabled() != null ? request.isEnabled() : Boolean.TRUE)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return preferenceRepository.save(preference)
                .map(this::toResponse);
    }

    public Mono<UserPreferenceResponse> update(Long id, UserPreferenceRequest request) {
        return preferenceRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("UserNotificationPreference", id)))
                .flatMap(existing -> {
                    existing.setUserId(request.userId());
                    existing.setChannel(request.channel());
                    existing.setTemplateName(request.templateName());
                    if (request.isEnabled() != null) {
                        existing.setIsEnabled(request.isEnabled());
                    }
                    existing.setUpdatedAt(Instant.now());
                    return preferenceRepository.save(existing);
                })
                .map(this::toResponse);
    }

    public Mono<Void> deleteById(Long id) {
        return preferenceRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("UserNotificationPreference", id)))
                .flatMap(preferenceRepository::delete);
    }

    private UserPreferenceResponse toResponse(UserNotificationPreference p) {
        return new UserPreferenceResponse(
                p.getId(),
                p.getUserId(),
                p.getChannel(),
                p.getTemplateName(),
                p.getIsEnabled(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
