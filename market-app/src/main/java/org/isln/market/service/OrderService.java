package org.isln.market.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import org.isln.market.configuration.security.Roles;
import org.isln.market.dto.UserDto;
import org.isln.market.model.Cart;
import org.isln.market.model.Order;
import org.isln.market.payment.PaymentService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderDataAccessService orderDataAccessService;
    private final ItemService itemService;
    private final PaymentService paymentService;
    private final UserService userService;

    @Secured(Roles.CLIENT)
    public Mono<List<Order>> findAllWithItems() {
        return userService.getCurrentUser()
                .map(UserDto::getName)
                .flatMap(orderDataAccessService::findAllWithItems);
    }

    @PostAuthorize("hasRole('ADMIN') or #returnObject?.user == authentication.name")
    public Mono<Order> findByIdWithItems(Long id) {
        return orderDataAccessService.findByIdWithItems(id);
    }

    @Secured(Roles.CLIENT)
    public Mono<Order> create() {
        return Mono.zip(itemService.getCart(), userService.getCurrentUser()).flatMap(this::createOrder);
    }

    private Mono<Order> createOrder(Tuple2<Cart, UserDto> t) {
        Cart cart = t.getT1();
        String user = t.getT2().getName();
        return paymentService.pay(cart.getTotalPrice())
                .then(Mono.just(new Order().setItems(cart).setUser(user)))
                .flatMap(orderDataAccessService::save)
                .flatMap(order -> itemService.cleanupCart().thenReturn(order));
    }
}
