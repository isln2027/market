package org.isln.market.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.isln.market.model.Cart;
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
                .flatMap(this::pay)
                .map(cart -> new Order().setItems(cart))
                .flatMap(orderDataAccessService::save)
                .flatMap(order -> itemService.cleanupCart().thenReturn(order));
    }

    private Mono<Cart> pay(Cart c) {
//        return paymentApi.pay(c.getTotalPrice()).flatMap(result -> {
//                    if (result == PaymentResult.SUCCESS) {
//                        return Mono.just(c);
//                    } else if (result == PaymentResult.INSUFFICIENT_FUNDS) {
//                        return Mono.error(new InsufficientFundsException());
//                    }
//                    return Mono.error(new RuntimeException("Unknown result '" + result + "'"));
//                }
//        );
        return Mono.empty();
    }
}
