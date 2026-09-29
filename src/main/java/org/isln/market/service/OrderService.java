package org.isln.market.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.market.exception.ObjectNotFoundException;
import org.isln.market.model.Cart;
import org.isln.market.model.Order;
import org.isln.market.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemService itemService;

    public List<Order> findAllWithItems() {
        return orderRepository.findAllWithItems();
    }

    public Order findByIdWithItems(Long id) {
        return orderRepository.findByIdWithItems(id).orElseThrow(() -> new ObjectNotFoundException("Заказ не найден!"));
    }

    public Order create() {
        Cart cart = itemService.getCart();
        Order order = new Order().setItems(cart);
        return orderRepository.save(order);
    }
}
