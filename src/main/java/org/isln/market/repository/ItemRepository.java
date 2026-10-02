package org.isln.market.repository;

import java.util.List;

import org.isln.market.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Override
    List<Item> findAll();

    Page<Item> findByTitleContainingOrDescriptionContaining(Pageable pageable, String title, String description);

    List<Item> findByCountGreaterThan(int count);
}
