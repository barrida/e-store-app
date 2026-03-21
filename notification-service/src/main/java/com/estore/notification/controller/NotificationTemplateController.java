package com.estore.notification.controller;

import com.estore.notification.dto.NotificationTemplateRequest;
import com.estore.notification.dto.NotificationTemplateResponse;
import com.estore.notification.service.NotificationTemplateService;
import com.estore.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class NotificationTemplateController {

    private final NotificationTemplateService templateService;

    @GetMapping("/api/notification-templates")
    public Mono<ApiResponse<List<NotificationTemplateResponse>>> getAllTemplates() {
        return templateService.findAll()
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/notification-templates/{id}")
    public Mono<ApiResponse<NotificationTemplateResponse>> getTemplateById(@PathVariable Long id) {
        return templateService.findById(id)
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/notification-templates/name/{name}")
    public Mono<ApiResponse<NotificationTemplateResponse>> getTemplateByName(@PathVariable String name) {
        return templateService.findByName(name)
                .map(ApiResponse::ok);
    }

    @PostMapping("/api/notification-templates")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<NotificationTemplateResponse>> createTemplate(
            @Valid @RequestBody NotificationTemplateRequest request) {
        return templateService.create(request)
                .map(t -> ApiResponse.ok("Notification template created successfully", t));
    }

    @PutMapping("/api/notification-templates/{id}")
    public Mono<ApiResponse<NotificationTemplateResponse>> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody NotificationTemplateRequest request) {
        return templateService.update(id, request)
                .map(t -> ApiResponse.ok("Notification template updated successfully", t));
    }

    @DeleteMapping("/api/notification-templates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTemplate(@PathVariable Long id) {
        return templateService.deleteById(id);
    }
}
