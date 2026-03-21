package com.estore.notification.controller;

import com.estore.notification.dto.UserPreferenceRequest;
import com.estore.notification.dto.UserPreferenceResponse;
import com.estore.notification.service.UserNotificationPreferenceService;
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
public class UserNotificationPreferenceController {

    private final UserNotificationPreferenceService preferenceService;

    @GetMapping("/api/users/{userId}/notification-preferences")
    public Mono<ApiResponse<List<UserPreferenceResponse>>> getPreferencesByUser(
            @PathVariable Long userId) {
        return preferenceService.findByUserId(userId)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PostMapping("/api/notification-preferences")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<UserPreferenceResponse>> createPreference(
            @Valid @RequestBody UserPreferenceRequest request) {
        return preferenceService.create(request)
                .map(p -> ApiResponse.ok("Notification preference created successfully", p));
    }

    @PutMapping("/api/notification-preferences/{id}")
    public Mono<ApiResponse<UserPreferenceResponse>> updatePreference(
            @PathVariable Long id,
            @Valid @RequestBody UserPreferenceRequest request) {
        return preferenceService.update(id, request)
                .map(p -> ApiResponse.ok("Notification preference updated successfully", p));
    }

    @DeleteMapping("/api/notification-preferences/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePreference(@PathVariable Long id) {
        return preferenceService.deleteById(id);
    }
}
