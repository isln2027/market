package org.isln.market.service;

import java.util.Map;

import org.isln.market.model.Item;
import org.isln.market.repository.projection.OrderedItem;
import org.springframework.stereotype.Service;

@Service
public class ItemMapper {
    public Item mapItem(OrderedItem orderedItem) {
        Item item = new Item();
        item.setId(orderedItem.getId());
        item.setTitle(orderedItem.getTitle());
        item.setDescription(orderedItem.getDescription());
        item.setPrice(orderedItem.getPrice());
        item.setCount(orderedItem.getCount());
        item.setImgPath(orderedItem.getImgPath());
        return item;
    }
}
