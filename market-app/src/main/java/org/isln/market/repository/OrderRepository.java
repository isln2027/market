package org.isln.market.repository;

import java.util.Map;

import reactor.core.publisher.Flux;

import org.isln.market.model.Order;
import org.isln.market.repository.projection.OrderedItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    @Query("""
            SELECT
                o.id            AS order_id,
                i.id            AS id,
                i.title         AS title,
                i.description   AS description,
                i.price         AS price,
                oi.item_count    AS count,
                i.img_path      AS img_path
            FROM orders o
            JOIN order_item_connection oi ON oi.order_id = o.id
            JOIN items i         ON i.id = oi.item_id
            """)
    Flux<OrderedItem> findAllWithItems();

    @Query("""
            SELECT
                i.id            AS id,
                i.title         AS title,
                i.description   AS description,
                i.price         AS price,
                oi.item_count    AS count,
                i.img_path      AS img_path
            FROM orders o
            JOIN order_item_connection oi ON oi.order_id = o.id
            JOIN items i         ON i.id = oi.item_id
            WHERE o.id = :id
            """)
    Flux<OrderedItem> findOrderedItems(Long id);
}
