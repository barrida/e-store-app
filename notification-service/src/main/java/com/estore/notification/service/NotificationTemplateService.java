package com.estore.notification.service;

import com.estore.notification.dto.NotificationTemplateRequest;
import com.estore.notification.dto.NotificationTemplateResponse;
import com.estore.notification.model.NotificationTemplate;
import com.estore.notification.repository.NotificationTemplateRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final NotificationTemplateRepository templateRepository;

    public Flux<NotificationTemplateResponse> findAll() {
        return templateRepository.findAll()
                .map(this::toResponse);
    }

    public Flux<NotificationTemplateResponse> findActive() {
        return templateRepository.findByIsActiveTrue()
                .map(this::toResponse);
    }

    public Mono<NotificationTemplateResponse> findById(Long id) {
        return templateRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("NotificationTemplate", id)))
                .map(this::toResponse);
    }

    public Mono<NotificationTemplateResponse> findByName(String name) {
        return templateRepository.findByName(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("NotificationTemplate", name)))
                .map(this::toResponse);
    }

    public Flux<NotificationTemplateResponse> findByChannel(String channel) {
        return templateRepository.findByChannel(channel)
                .map(this::toResponse);
    }

    public Mono<NotificationTemplateResponse> create(NotificationTemplateRequest request) {
        Instant now = Instant.now();
        NotificationTemplate template = NotificationTemplate.builder()
                .name(request.name())
                .channel(request.channel())
                .subject(request.subject())
                .bodyHtml(request.bodyHtml())
                .bodyText(request.bodyText())
                .variables(request.variables())
                .isActive(request.isActive() != null ? request.isActive() : Boolean.TRUE)
                .version(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return templateRepository.save(template)
                .map(this::toResponse);
    }

    public Mono<NotificationTemplateResponse> update(Long id, NotificationTemplateRequest request) {
        return templateRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("NotificationTemplate", id)))
                .flatMap(existing -> {
                    existing.setName(request.name());
                    existing.setChannel(request.channel());
                    existing.setSubject(request.subject());
                    existing.setBodyHtml(request.bodyHtml());
                    existing.setBodyText(request.bodyText());
                    existing.setVariables(request.variables());
                    if (request.isActive() != null) {
                        existing.setIsActive(request.isActive());
                    }
                    existing.setVersion(existing.getVersion() + 1);
                    existing.setUpdatedAt(Instant.now());
                    return templateRepository.save(existing);
                })
                .map(this::toResponse);
    }

    public Mono<Void> deleteById(Long id) {
        return templateRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("NotificationTemplate", id)))
                .flatMap(templateRepository::delete);
    }

    private NotificationTemplateResponse toResponse(NotificationTemplate t) {
        return new NotificationTemplateResponse(
                t.getId(),
                t.getName(),
                t.getChannel(),
                t.getSubject(),
                t.getBodyHtml(),
                t.getBodyText(),
                t.getVariables(),
                t.getIsActive(),
                t.getVersion(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
