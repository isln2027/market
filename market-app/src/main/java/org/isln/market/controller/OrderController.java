package org.isln.market.controller;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.model.Order;
import org.isln.market.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders")
    public Mono<Rendering> findAll() {
        return Mono.just(
                Rendering.view("orders")
                        .modelAttribute("orders", orderService.findAllWithItems())
                        .build()
        );
    }

    @GetMapping("/orders/{id}")
    public Mono<Rendering> findById(@PathVariable Long id, @ModelAttribute OrderRequest request) {
        return orderService.findByIdWithItems(id)
                .map(
                        order -> Rendering.view("order")
                                .modelAttribute("order", order)
                                .modelAttribute("newOrder", request.getNewOrder())
                                .build()
                );
    }

    @PostMapping("/buy")
    public Mono<Rendering> createOrder() {
        return orderService.create().map(newOrder -> Rendering.redirectTo(newOrderUrl(newOrder)).build());
    }

    private static String newOrderUrl(Order newOrder) {
        return "/orders/%s?newOrder=true".formatted(newOrder.getId());
    }
}
