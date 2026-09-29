package org.isln.market.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.market.model.Order;
import org.isln.market.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders")
    public String findAll(Model model) {
        List<Order> orders = orderService.findAllWithItems();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String findById(@PathVariable Long id, @RequestParam(defaultValue = "false") Boolean newOrder, Model model) {
        Order order = orderService.findByIdWithItems(id);
        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }


}
