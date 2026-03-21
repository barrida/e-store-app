package com.estore.notification.controller;

import com.estore.notification.dto.NotificationResponse;
import com.estore.notification.dto.SendNotificationRequest;
import com.estore.notification.service.NotificationService;
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
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/api/notifications")
    public Mono<ApiResponse<List<NotificationResponse>>> getNotifications(
            @RequestParam(required = false) Long userId) {
        if (userId != null) {
            return notificationService.findByUserId(userId)
                    .collectList()
                    .map(ApiResponse::ok);
        }
        return notificationService.findAll()
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/notifications/{id}")
    public Mono<ApiResponse<NotificationResponse>> getNotificationById(@PathVariable Long id) {
        return notificationService.findById(id)
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/users/{userId}/notifications")
    public Mono<ApiResponse<List<NotificationResponse>>> getNotificationsByUser(
            @PathVariable Long userId) {
        return notificationService.findByUserId(userId)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PostMapping("/api/notifications/send")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        return notificationService.send(request)
                .map(n -> ApiResponse.ok("Notification queued successfully", n));
    }

    @PutMapping("/api/notifications/{id}/status")
    public Mono<ApiResponse<NotificationResponse>> updateNotificationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return notificationService.updateStatus(id, status)
                .map(n -> ApiResponse.ok("Notification status updated", n));
    }
}
