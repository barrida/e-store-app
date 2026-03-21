package com.estore.user.service;

import com.estore.shared.exception.ResourceNotFoundException;
import com.estore.user.dto.AddressRequest;
import com.estore.user.dto.AddressResponse;
import com.estore.user.model.Address;
import com.estore.user.model.AddressType;
import com.estore.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public Flux<AddressResponse> findByUserId(Long userId) {
        return addressRepository.findByUserId(userId).map(this::toAddressResponse);
    }

    public Mono<AddressResponse> findById(Long id) {
        return addressRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Address", id)))
                .map(this::toAddressResponse);
    }

    public Mono<AddressResponse> create(AddressRequest request) {
        Instant now = Instant.now();
        String addressType = request.addressType() != null
                ? request.addressType()
                : AddressType.SHIPPING.getValue();

        Address address = Address.builder()
                .userId(request.userId())
                .addressType(addressType)
                .isDefault(Boolean.TRUE.equals(request.isDefault()))
                .label(request.label())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .company(request.company())
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .stateProvince(request.stateProvince())
                .postalCode(request.postalCode())
                .countryCode(request.countryCode())
                .phoneNumber(request.phoneNumber())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return addressRepository.save(address).map(this::toAddressResponse);
    }

    public Mono<AddressResponse> update(Long id, AddressRequest request) {
        return addressRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Address", id)))
                .flatMap(existing -> {
                    Instant now = Instant.now();
                    if (request.addressType() != null) existing.setAddressType(request.addressType());
                    if (request.isDefault() != null) existing.setDefault(request.isDefault());
                    if (request.label() != null) existing.setLabel(request.label());
                    if (request.firstName() != null) existing.setFirstName(request.firstName());
                    if (request.lastName() != null) existing.setLastName(request.lastName());
                    if (request.company() != null) existing.setCompany(request.company());
                    if (request.addressLine1() != null) existing.setAddressLine1(request.addressLine1());
                    if (request.addressLine2() != null) existing.setAddressLine2(request.addressLine2());
                    if (request.city() != null) existing.setCity(request.city());
                    if (request.stateProvince() != null) existing.setStateProvince(request.stateProvince());
                    if (request.postalCode() != null) existing.setPostalCode(request.postalCode());
                    if (request.countryCode() != null) existing.setCountryCode(request.countryCode());
                    if (request.phoneNumber() != null) existing.setPhoneNumber(request.phoneNumber());
                    existing.setUpdatedAt(now);
                    return addressRepository.save(existing);
                })
                .map(this::toAddressResponse);
    }

    public Mono<Void> delete(Long id) {
        return addressRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Address", id)))
                .flatMap(addressRepository::delete);
    }

    private AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getUserId(),
                address.getAddressType(),
                address.isDefault(),
                address.getLabel(),
                address.getFirstName(),
                address.getLastName(),
                address.getCompany(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getStateProvince(),
                address.getPostalCode(),
                address.getCountryCode(),
                address.getPhoneNumber(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }
}
