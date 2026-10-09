package org.isln.market.controller;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.controller.request.CartActionRequest;
import org.isln.market.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final ItemService itemService;

    @GetMapping("/cart/items")
    public Mono<Rendering> getCart() {
        return itemService.getCart().map(
                cart -> Rendering.view("cart")
                        .modelAttribute("items", cart)
                        .modelAttribute("total", cart.getTotalPrice())
                        .build());
    }

    @PostMapping("/cart/items")
    public Mono<Rendering> performAction(@ModelAttribute CartActionRequest request) {
        return itemService.performAction(request.getId(), request.getAction())
                .map(item -> Rendering.redirectTo("/cart/items").build());
    }
}
