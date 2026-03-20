package com.estore.user.controller;

import com.estore.shared.dto.ApiResponse;
import com.estore.user.dto.AddressRequest;
import com.estore.user.dto.AddressResponse;
import com.estore.user.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/api/users/{userId}/addresses")
    public Mono<ApiResponse<List<AddressResponse>>> getAddressesByUser(@PathVariable Long userId) {
        return addressService.findByUserId(userId)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/addresses/{id}")
    public Mono<ApiResponse<AddressResponse>> getAddressById(@PathVariable Long id) {
        return addressService.findById(id)
                .map(ApiResponse::ok);
    }

    @PostMapping("/api/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<AddressResponse>> createAddress(@Valid @RequestBody AddressRequest request) {
        return addressService.create(request)
                .map(address -> ApiResponse.ok("Address created successfully", address));
    }

    @PutMapping("/api/addresses/{id}")
    public Mono<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        return addressService.update(id, request)
                .map(address -> ApiResponse.ok("Address updated successfully", address));
    }

    @DeleteMapping("/api/addresses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAddress(@PathVariable Long id) {
        return addressService.delete(id);
    }
}
