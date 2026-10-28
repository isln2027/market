package org.isln.market.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.isln.market.model.Item;
import org.isln.market.model.Order;
import org.isln.market.model.OrderedItemConnection;
import org.isln.market.repository.OrderRepository;
import org.isln.market.repository.OrderedItemConnectionRepository;
import org.isln.market.repository.projection.OrderedItem;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDataAccessService {
    private final OrderRepository orderRepository;
    private final OrderedItemConnectionRepository connectionRepository;
    private final ItemMapper mapper;

    public Flux<Order> findAllWithItems() {
        return orderRepository.findAllWithItems()
                .groupBy(OrderedItem::getOrderId)
                .flatMap(
                        orderedItems -> orderedItems
                                .map(mapper::map)
                                .collectList()
                                .map(items -> new Order().setId(orderedItems.key()).setItems(items))
                );
    }

    public Mono<Order> findByIdWithItems(Long id) {
        Flux<Item> items =  orderRepository.findOrderedItems(id).map(mapper::map);
        return orderRepository.findById(id)
                .flatMap(o -> items.collectList().flatMap(list -> Mono.just(o.setItems(list))));
    }

    public Mono<Order> save(Order order) {
        return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    Long orderId = savedOrder.getId();
                    Flux<OrderedItemConnection> connectionsFlux = Flux.fromStream(order.getItems().stream())
                            .map(item -> new OrderedItemConnection()
                                    .setOrderId(orderId)
                                    .setItemId(item.getId())
                                    .setItemCount(item.getCount())
                            )
                            .flatMap(connectionRepository::save);
                    return connectionsFlux.then(Mono.just(orderId));
                })
                .flatMap(this::findByIdWithItems);
    }
}
