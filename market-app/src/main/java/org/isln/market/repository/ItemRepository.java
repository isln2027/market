package org.isln.market.repository;

import java.util.List;
import java.util.Map;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.isln.market.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ItemRepository extends R2dbcRepository<Item, Long> {
    Flux<Item> findBy(Pageable pageable);

    @Query("SELECT items.count as count FROM items WHERE items.id = :id")
    Mono<Integer> findCountById(Long id);

    @Query("SELECT items.id as id, items.count as count FROM items WHERE items.id IN (:ids)")
    Flux<Item> findCountByIdIn(List<Long> ids);

    Flux<Item> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);

    Mono<Long> countByTitleContainingOrDescriptionContaining(String title, String description);

    Flux<Item> findByCountGreaterThan(int count);
}
