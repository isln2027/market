package org.isln.market.repository;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.isln.market.model.CartItem;
import org.isln.market.model.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CartRepository extends ReactiveCrudRepository<CartItem, Long> {
    Mono<CartItem> findCountByUserAndItemId(String login, Long id);

    Flux<CartItem> findCountByUserAndItemIdIn(String login, List<Long> ids);

    @Query("""
            SELECT
                i.id            AS id,
                i.title         AS title,
                i.description   AS description,
                i.price         AS price,
                ci.count        AS count,
                i.img_path      AS img_path
            FROM cart_items ci
            JOIN items i        ON i.id = ci.item_id
            WHERE ci.user = :login
            """)
    Flux<Item> findCartItemsByUser(String login);

    Flux<CartItem> findByUser(String user);
    Mono<CartItem> findByUserAndItemId(String user, Long item_id);
}
