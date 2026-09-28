package org.isln.market.repository;

import java.util.List;

import org.isln.market.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Long> {
    @Override
    List<Item> findAll();
}
