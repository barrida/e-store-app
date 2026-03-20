package com.estore.order.service;

import com.estore.order.dto.ShipmentRequest;
import com.estore.order.dto.ShipmentResponse;
import com.estore.order.model.Shipment;
import com.estore.order.repository.ShipmentRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public Flux<ShipmentResponse> findByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .map(this::toResponse);
    }

    public Mono<ShipmentResponse> findById(Long id) {
        return shipmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Shipment", id)))
                .map(this::toResponse);
    }

    public Mono<ShipmentResponse> createShipment(ShipmentRequest request) {
        Instant now = Instant.now();
        Shipment shipment = Shipment.builder()
                .orderId(request.orderId())
                .trackingNumber(request.trackingNumber())
                .carrier(request.carrier())
                .shippingMethod(request.shippingMethod())
                .estimatedDelivery(request.estimatedDelivery())
                .trackingUrl(request.trackingUrl())
                .status("pending")
                .createdAt(now)
                .updatedAt(now)
                .build();
        return shipmentRepository.save(shipment).map(this::toResponse);
    }

    public Mono<ShipmentResponse> updateShipment(Long id, ShipmentRequest request) {
        return shipmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Shipment", id)))
                .flatMap(shipment -> {
                    if (request.trackingNumber() != null) shipment.setTrackingNumber(request.trackingNumber());
                    if (request.carrier() != null) shipment.setCarrier(request.carrier());
                    if (request.shippingMethod() != null) shipment.setShippingMethod(request.shippingMethod());
                    if (request.estimatedDelivery() != null) shipment.setEstimatedDelivery(request.estimatedDelivery());
                    if (request.trackingUrl() != null) shipment.setTrackingUrl(request.trackingUrl());
                    shipment.setUpdatedAt(Instant.now());
                    return shipmentRepository.save(shipment);
                })
                .map(this::toResponse);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private ShipmentResponse toResponse(Shipment s) {
        return new ShipmentResponse(
                s.getId(),
                s.getOrderId(),
                s.getTrackingNumber(),
                s.getCarrier(),
                s.getShippingMethod(),
                s.getEstimatedDelivery(),
                s.getShippedAt(),
                s.getDeliveredAt(),
                s.getStatus(),
                s.getTrackingUrl(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
