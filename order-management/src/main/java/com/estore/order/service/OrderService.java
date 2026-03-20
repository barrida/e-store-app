package com.estore.order.service;

import com.estore.order.dto.*;
import com.estore.order.model.Order;
import com.estore.order.model.OrderItem;
import com.estore.order.model.OrderStatus;
import com.estore.order.repository.OrderItemRepository;
import com.estore.order.repository.OrderRepository;
import com.estore.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Flux<OrderResponse> findAll() {
        return orderRepository.findAll()
                .flatMap(this::enrichWithItems);
    }

    public Flux<OrderResponse> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .flatMap(this::enrichWithItems);
    }

    public Mono<OrderResponse> findById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Order", id)))
                .flatMap(this::enrichWithItems);
    }

    public Mono<OrderResponse> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Order", orderNumber)))
                .flatMap(this::enrichWithItems);
    }

    public Flux<OrderResponse> findByStatus(String status) {
        return orderRepository.findByStatus(status)
                .flatMap(this::enrichWithItems);
    }

    public Mono<OrderResponse> createOrder(OrderRequest request) {
        Instant now = Instant.now();
        Order order = Order.builder()
                .orderNumber("PENDING")
                .userId(request.userId())
                .status(OrderStatus.PENDING.toDbValue())
                .paymentStatus(com.estore.order.model.PaymentStatus.PENDING.toDbValue())
                .subtotal(request.subtotal())
                .discountAmount(nullSafe(request.discountAmount()))
                .shippingAmount(nullSafe(request.shippingAmount()))
                .taxAmount(nullSafe(request.taxAmount()))
                .totalAmount(request.totalAmount())
                .currency(request.currency() != null ? request.currency() : "USD")
                .shippingAddress(request.shippingAddress())
                .billingAddress(request.billingAddress())
                .couponCode(request.couponCode())
                .couponDiscount(nullSafe(request.couponDiscount()))
                .notes(request.notes())
                .ipAddress(request.ipAddress())
                .userAgent(request.userAgent())
                .placedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return orderRepository.save(order)
                .flatMap(saved -> {
                    String orderNumber = generateOrderNumber(saved.getId());
                    saved.setOrderNumber(orderNumber);
                    saved.setUpdatedAt(Instant.now());
                    return orderRepository.save(saved);
                })
                .flatMap(saved -> {
                    List<OrderItem> items = request.items().stream()
                            .map(itemReq -> buildOrderItem(itemReq, saved.getId()))
                            .toList();
                    return orderItemRepository.saveAll(items)
                            .collectList()
                            .map(savedItems -> toResponse(saved, savedItems));
                });
    }

    public Mono<OrderResponse> updateStatus(Long id, String status) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Order", id)))
                .flatMap(order -> {
                    Instant now = Instant.now();
                    order.setStatus(status);
                    order.setUpdatedAt(now);
                    applyStatusTimestamp(order, status, now);
                    return orderRepository.save(order);
                })
                .flatMap(this::enrichWithItems);
    }

    public Mono<Void> deleteById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Order", id)))
                .flatMap(order -> orderItemRepository.findByOrderId(order.getId())
                        .flatMap(orderItemRepository::delete)
                        .then(orderRepository.delete(order)));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Mono<OrderResponse> enrichWithItems(Order order) {
        return orderItemRepository.findByOrderId(order.getId())
                .collectList()
                .map(items -> toResponse(order, items));
    }

    private String generateOrderNumber(Long id) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ORD-" + date + "-" + String.format("%06d", id);
    }

    private void applyStatusTimestamp(Order order, String status, Instant now) {
        switch (status.toLowerCase()) {
            case "confirmed" -> order.setConfirmedAt(now);
            case "shipped"   -> order.setShippedAt(now);
            case "delivered" -> order.setDeliveredAt(now);
            case "cancelled" -> order.setCancelledAt(now);
            default -> { /* no extra timestamp */ }
        }
    }

    private OrderItem buildOrderItem(OrderItemRequest req, Long orderId) {
        Instant now = Instant.now();
        return OrderItem.builder()
                .orderId(orderId)
                .productId(req.productId())
                .productSku(req.productSku())
                .productName(req.productName())
                .productImageUrl(req.productImageUrl())
                .quantity(req.quantity())
                .unitPrice(req.unitPrice())
                .discountAmount(nullSafe(req.discountAmount()))
                .taxAmount(nullSafe(req.taxAmount()))
                .totalPrice(req.totalPrice())
                .currency(req.currency() != null ? req.currency() : "USD")
                .productOptions(req.productOptions())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private OrderResponse toResponse(Order o, List<OrderItem> items) {
        return new OrderResponse(
                o.getId(),
                o.getOrderNumber(),
                o.getUserId(),
                o.getStatus(),
                o.getPaymentStatus(),
                o.getSubtotal(),
                o.getDiscountAmount(),
                o.getShippingAmount(),
                o.getTaxAmount(),
                o.getTotalAmount(),
                o.getCurrency(),
                o.getShippingAddress(),
                o.getBillingAddress(),
                o.getCouponCode(),
                o.getCouponDiscount(),
                o.getNotes(),
                o.getIpAddress(),
                o.getUserAgent(),
                o.getPlacedAt(),
                o.getConfirmedAt(),
                o.getShippedAt(),
                o.getDeliveredAt(),
                o.getCancelledAt(),
                o.getCreatedAt(),
                o.getUpdatedAt(),
                items.stream().map(this::toItemResponse).toList()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem i) {
        return new OrderItemResponse(
                i.getId(),
                i.getOrderId(),
                i.getProductId(),
                i.getProductSku(),
                i.getProductName(),
                i.getProductImageUrl(),
                i.getQuantity(),
                i.getUnitPrice(),
                i.getDiscountAmount(),
                i.getTaxAmount(),
                i.getTotalPrice(),
                i.getCurrency(),
                i.getProductOptions(),
                i.getCreatedAt(),
                i.getUpdatedAt()
        );
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
