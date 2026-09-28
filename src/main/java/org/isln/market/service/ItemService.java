package org.isln.market.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.model.Item;
import org.isln.market.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public List<Item> find(ItemRequestParameters parameters) {
        // todo use parameters
        return itemRepository.findAll();
    }
}
