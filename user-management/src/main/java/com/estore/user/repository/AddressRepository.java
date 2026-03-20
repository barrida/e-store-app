package com.estore.user.repository;

import com.estore.user.model.Address;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AddressRepository extends ReactiveCrudRepository<Address, Long> {

    Flux<Address> findByUserId(Long userId);

    Mono<Address> findByUserIdAndIsDefaultTrue(Long userId);
}
