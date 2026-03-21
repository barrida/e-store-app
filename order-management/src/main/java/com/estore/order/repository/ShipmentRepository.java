package com.estore.order.repository;

import com.estore.order.model.Shipment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ShipmentRepository extends ReactiveCrudRepository<Shipment, Long> {

    Flux<Shipment> findByOrderId(Long orderId);
}
