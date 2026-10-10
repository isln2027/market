package org.isln.market.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.isln.market.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ItemRepository extends R2dbcRepository<Item, Long> {
    Flux<Item> findBy(Pageable pageable);

    Flux<Item> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);

    Mono<Long> countByTitleContainingOrDescriptionContaining(String title, String description);

    Flux<Item> findByCountGreaterThan(int count);
}
