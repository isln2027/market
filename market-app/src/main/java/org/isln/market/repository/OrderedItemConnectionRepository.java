package org.isln.market.repository;

import org.isln.market.model.OrderedItemConnection;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface OrderedItemConnectionRepository extends ReactiveCrudRepository<OrderedItemConnection, Long> {
}
