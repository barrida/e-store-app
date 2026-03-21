package com.estore.order.controller;

import com.estore.order.dto.ShipmentRequest;
import com.estore.order.dto.ShipmentResponse;
import com.estore.order.service.ShipmentService;
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
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping("/api/orders/{orderId}/shipments")
    public Mono<ApiResponse<List<ShipmentResponse>>> getShipmentsByOrder(@PathVariable Long orderId) {
        return shipmentService.findByOrderId(orderId)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PostMapping("/api/shipments")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<ShipmentResponse>> createShipment(@Valid @RequestBody ShipmentRequest request) {
        return shipmentService.createShipment(request)
                .map(s -> ApiResponse.ok("Shipment created successfully", s));
    }

    @PutMapping("/api/shipments/{id}")
    public Mono<ApiResponse<ShipmentResponse>> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentRequest request) {
        return shipmentService.updateShipment(id, request)
                .map(s -> ApiResponse.ok("Shipment updated successfully", s));
    }
}
