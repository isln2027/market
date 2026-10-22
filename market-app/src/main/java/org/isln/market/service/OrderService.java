package org.isln.market.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.isln.market.model.Order;
import org.isln.payment.api.PaymentApi;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderDataAccessService orderDataAccessService;
    private final ItemService itemService;
    private final PaymentApi paymentApi;

    public Flux<Order> findAllWithItems() {
        return orderDataAccessService.findAllWithItems();
    }

    public Mono<Order> findByIdWithItems(Long id) {
        return orderDataAccessService.findByIdWithItems(id);
    }

    public Mono<Order> create() {
        return itemService.getCart()
                .flatMap(c -> paymentApi.pay(c.getTotalPrice()).then(Mono.just(c)))
                .map(cart -> new Order().setItems(cart))
                .flatMap(orderDataAccessService::save)
                .flatMap(order -> itemService.cleanupCart().thenReturn(order));
    }
}
