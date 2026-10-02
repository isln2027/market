package org.isln.market.controller;

import lombok.RequiredArgsConstructor;

import org.isln.market.dto.CartAction;
import org.isln.market.model.Cart;
import org.isln.market.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final ItemService itemService;

    @GetMapping("/cart/items")
    public String getCart(Model model) {
        Cart cart = itemService.getCart();
        model.addAttribute("items", cart);
        model.addAttribute("total", cart.getTotalPrice());
        return "cart";
    }

    @PostMapping("/cart/items")
    public String performAction(@RequestParam Long id, @RequestParam CartAction action) {
        itemService.performAction(id, action);
        return "redirect:/cart/items";
    }
}
