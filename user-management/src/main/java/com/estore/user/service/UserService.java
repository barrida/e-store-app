package com.estore.user.service;

import com.estore.shared.exception.ResourceNotFoundException;
import com.estore.user.dto.UserProfileRequest;
import com.estore.user.dto.UserProfileResponse;
import com.estore.user.dto.UserRegistrationRequest;
import com.estore.user.dto.UserResponse;
import com.estore.user.model.User;
import com.estore.user.model.UserProfile;
import com.estore.user.model.UserStatus;
import com.estore.user.model.AuthProvider;
import com.estore.user.repository.UserProfileRepository;
import com.estore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public Flux<UserResponse> findAll() {
        return userRepository.findAll().map(this::toUserResponse);
    }

    public Mono<UserResponse> findById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", id)))
                .map(this::toUserResponse);
    }

    public Mono<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", email)))
                .map(this::toUserResponse);
    }

    public Flux<UserResponse> findByStatus(String status) {
        return userRepository.findByStatus(status).map(this::toUserResponse);
    }

    @Transactional
    public Mono<UserResponse> register(UserRegistrationRequest request) {
        Instant now = Instant.now();

        User user = User.builder()
                .email(request.email())
                // TODO: Replace with BCrypt hash when security is configured: passwordEncoder.encode(request.password())
                .passwordHash(request.password())
                .emailVerified(false)
                .authProvider(AuthProvider.LOCAL.getValue())
                .status(UserStatus.PENDING_VERIFICATION.getValue())
                .failedLoginCount((short) 0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return userRepository.save(user)
                .flatMap(savedUser -> {
                    UserProfile profile = UserProfile.builder()
                            .userId(savedUser.getId())
                            .firstName(request.firstName())
                            .lastName(request.lastName())
                            .locale("en-US")
                            .timezone("UTC")
                            .phoneVerified(false)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return userProfileRepository.save(profile).thenReturn(savedUser);
                })
                .map(this::toUserResponse);
    }

    public Mono<UserResponse> update(Long id, UserProfileRequest request) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", id)))
                .flatMap(existingUser -> userProfileRepository.findByUserId(id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("UserProfile", id)))
                        .flatMap(profile -> {
                            Instant now = Instant.now();
                            if (request.firstName() != null) profile.setFirstName(request.firstName());
                            if (request.lastName() != null) profile.setLastName(request.lastName());
                            if (request.displayName() != null) profile.setDisplayName(request.displayName());
                            if (request.dateOfBirth() != null) profile.setDateOfBirth(request.dateOfBirth());
                            if (request.gender() != null) profile.setGender(request.gender());
                            if (request.phoneNumber() != null) profile.setPhoneNumber(request.phoneNumber());
                            if (request.avatarUrl() != null) profile.setAvatarUrl(request.avatarUrl());
                            if (request.bio() != null) profile.setBio(request.bio());
                            if (request.locale() != null) profile.setLocale(request.locale());
                            if (request.timezone() != null) profile.setTimezone(request.timezone());
                            profile.setUpdatedAt(now);
                            return userProfileRepository.save(profile);
                        })
                        .thenReturn(existingUser))
                .map(this::toUserResponse);
    }

    public Mono<Void> delete(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", id)))
                .flatMap(user -> userProfileRepository.findByUserId(id)
                        .flatMap(profile -> userProfileRepository.delete(profile).thenReturn(user))
                        .defaultIfEmpty(user)
                        .flatMap(userRepository::delete));
    }

    public Mono<UserProfileResponse> getProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("UserProfile", userId)))
                .map(this::toUserProfileResponse);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getAuthProvider(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private UserProfileResponse toUserProfileResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getUserId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getDisplayName(),
                profile.getDateOfBirth(),
                profile.getGender(),
                profile.getPhoneNumber(),
                profile.isPhoneVerified(),
                profile.getAvatarUrl(),
                profile.getBio(),
                profile.getLocale(),
                profile.getTimezone(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
