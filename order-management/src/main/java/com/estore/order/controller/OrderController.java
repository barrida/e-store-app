package com.estore.order.controller;

import com.estore.order.dto.OrderRequest;
import com.estore.order.dto.OrderResponse;
import com.estore.order.service.OrderService;
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
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/api/orders")
    public Mono<ApiResponse<List<OrderResponse>>> getOrders(
            @RequestParam(required = false) Long userId) {
        if (userId != null) {
            return orderService.findByUserId(userId)
                    .collectList()
                    .map(ApiResponse::ok);
        }
        return orderService.findAll()
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/orders/{id}")
    public Mono<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/orders/number/{orderNumber}")
    public Mono<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        return orderService.findByOrderNumber(orderNumber)
                .map(ApiResponse::ok);
    }

    @GetMapping("/api/users/{userId}/orders")
    public Mono<ApiResponse<List<OrderResponse>>> getOrdersByUser(@PathVariable Long userId) {
        return orderService.findByUserId(userId)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PostMapping("/api/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request)
                .map(order -> ApiResponse.ok("Order placed successfully", order));
    }

    @PutMapping("/api/orders/{id}/status")
    public Mono<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return orderService.updateStatus(id, status)
                .map(order -> ApiResponse.ok("Order status updated", order));
    }

    @DeleteMapping("/api/orders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteOrder(@PathVariable Long id) {
        return orderService.deleteById(id);
    }
}
