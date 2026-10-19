package org.isln.market.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.isln.market.model.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderDataAccessService orderDataAccessService;
    private final ItemService itemService;

    public Flux<Order> findAllWithItems() {
        return orderDataAccessService.findAllWithItems();
    }

    public Mono<Order> findByIdWithItems(Long id) {
        return orderDataAccessService.findByIdWithItems(id);
    }

    public Mono<Order> create() {
        return itemService.getCart()
                .map(cart -> new Order().setItems(cart))
                .flatMap(orderDataAccessService::save)
                .flatMap(order -> itemService.cleanupCart().thenReturn(order));
    }
}
