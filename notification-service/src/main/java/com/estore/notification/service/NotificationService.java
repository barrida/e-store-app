package com.estore.notification.service;

import com.estore.notification.dto.NotificationResponse;
import com.estore.notification.dto.SendNotificationRequest;
import com.estore.notification.model.Notification;
import com.estore.notification.model.NotificationStatus;
import com.estore.notification.repository.NotificationRepository;
import com.estore.notification.repository.NotificationTemplateRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;

    public Flux<NotificationResponse> findAll() {
        return notificationRepository.findAll()
                .map(this::toResponse);
    }

    public Flux<NotificationResponse> findByUserId(Long userId) {
        return notificationRepository.findByUserId(userId)
                .map(this::toResponse);
    }

    public Mono<NotificationResponse> findById(Long id) {
        return notificationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Notification", id)))
                .map(this::toResponse);
    }

    public Flux<NotificationResponse> findByStatus(String status) {
        return notificationRepository.findByStatus(status)
                .map(this::toResponse);
    }

    public Flux<NotificationResponse> findByChannel(String channel) {
        return notificationRepository.findByChannel(channel)
                .map(this::toResponse);
    }

    public Flux<NotificationResponse> findByReference(String referenceType, Long referenceId) {
        return notificationRepository.findByReferenceTypeAndReferenceId(referenceType, referenceId)
                .map(this::toResponse);
    }

    public Mono<NotificationResponse> send(SendNotificationRequest request) {
        Instant now = Instant.now();

        if (request.templateName() != null && !request.templateName().isBlank()) {
            return templateRepository.findByName(request.templateName())
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("NotificationTemplate", request.templateName())))
                    .flatMap(template -> {
                        Notification notification = Notification.builder()
                                .templateId(template.getId())
                                .channel(request.channel() != null ? request.channel() : template.getChannel())
                                .status(NotificationStatus.QUEUED.toDbValue())
                                .userId(request.userId())
                                .recipientAddress(request.recipientAddress())
                                .subject(request.subject() != null ? request.subject() : template.getSubject())
                                .body(request.body() != null ? request.body() : template.getBodyText())
                                .referenceType(request.referenceType())
                                .referenceId(request.referenceId())
                                .retryCount((short) 0)
                                .queuedAt(now)
                                .createdAt(now)
                                .updatedAt(now)
                                .build();
                        return notificationRepository.save(notification);
                    })
                    .map(this::toResponse);
        }

        Notification notification = Notification.builder()
                .channel(request.channel())
                .status(NotificationStatus.QUEUED.toDbValue())
                .userId(request.userId())
                .recipientAddress(request.recipientAddress())
                .subject(request.subject())
                .body(request.body())
                .referenceType(request.referenceType())
                .referenceId(request.referenceId())
                .retryCount((short) 0)
                .queuedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return notificationRepository.save(notification)
                .map(this::toResponse);
    }

    public Mono<NotificationResponse> updateStatus(Long id, String status) {
        return notificationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Notification", id)))
                .flatMap(notification -> {
                    Instant now = Instant.now();
                    notification.setStatus(status);
                    notification.setUpdatedAt(now);
                    applyStatusTimestamp(notification, status, now);
                    return notificationRepository.save(notification);
                })
                .map(this::toResponse);
    }

    public Mono<Void> deleteById(Long id) {
        return notificationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Notification", id)))
                .flatMap(notificationRepository::delete);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void applyStatusTimestamp(Notification notification, String status, Instant now) {
        switch (status.toLowerCase()) {
            case "sent"      -> notification.setSentAt(now);
            case "delivered" -> notification.setDeliveredAt(now);
            case "failed", "bounced" -> notification.setFailedAt(now);
            default -> { /* no extra timestamp */ }
        }
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getTemplateId(),
                n.getChannel(),
                n.getStatus(),
                n.getUserId(),
                n.getRecipientAddress(),
                n.getSubject(),
                n.getBody(),
                n.getReferenceType(),
                n.getReferenceId(),
                n.getProvider(),
                n.getProviderMessageId(),
                n.getProviderResponse(),
                n.getErrorMessage(),
                n.getRetryCount(),
                n.getNextRetryAt(),
                n.getQueuedAt(),
                n.getSentAt(),
                n.getDeliveredAt(),
                n.getFailedAt(),
                n.getCreatedAt(),
                n.getUpdatedAt()
        );
    }
}
