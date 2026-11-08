package org.isln.market.controller.mapper;

import org.isln.market.dto.ItemDto;
import org.isln.market.model.Item;
import org.springframework.stereotype.Service;

@Service
public class Mapper {
    public Item map(ItemDto itemDto) {
        return new Item()
                .setTitle(itemDto.getTitle())
                .setPrice(itemDto.getPrice())
                .setDescription(itemDto.getDescription())
                .setImgPath(itemDto.getImgPath());
    }
}
