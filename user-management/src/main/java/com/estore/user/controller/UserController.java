package com.estore.user.controller;

import com.estore.shared.dto.ApiResponse;
import com.estore.user.dto.UserProfileRequest;
import com.estore.user.dto.UserProfileResponse;
import com.estore.user.dto.UserRegistrationRequest;
import com.estore.user.dto.UserResponse;
import com.estore.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Mono<ApiResponse<List<UserResponse>>> getAllUsers() {
        return userService.findAll()
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ApiResponse::ok);
    }

    @GetMapping("/email/{email}")
    public Mono<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ApiResponse::ok);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        return userService.register(request)
                .map(user -> ApiResponse.ok("User registered successfully", user));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserProfileRequest request) {
        return userService.update(id, request)
                .map(user -> ApiResponse.ok("User updated successfully", user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.delete(id);
    }

    @GetMapping("/{id}/profile")
    public Mono<ApiResponse<UserProfileResponse>> getProfile(@PathVariable Long id) {
        return userService.getProfile(id)
                .map(ApiResponse::ok);
    }
}
