package org.isln.market.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.CartAction;
import org.isln.market.dto.SortType;
import org.isln.market.exception.ObjectNotFoundException;
import org.isln.market.model.Item;
import org.isln.market.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    private static final Map<SortType, Sort> SORT = Map.of(
            SortType.NO, Sort.unsorted(),
            SortType.ALPHA, Sort.by(Sort.Order.asc("title")),
            SortType.PRICE, Sort.by(Sort.Order.asc("price"))
    );

    public Page<Item> find(ItemRequestParameters parameters) {
        SortType sortType = parameters.getSort();
        if (!SORT.containsKey(sortType)) {
            throw new IllegalArgumentException("Unknown sort type '" + sortType + "'");
        }
        Sort sort = SORT.get(sortType);
        String search = parameters.getSearch();
        PageRequest pageRequest = PageRequest.of(parameters.getPageNumber() - 1, parameters.getPageSize(), sort);
        return search == null ? find(pageRequest) : find(pageRequest, search);
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Товар не найден!"));
    }

    @Transactional
    public Item performAction(Long id, CartAction action) {
        Item item = findById(id);
        if (action == CartAction.PLUS) {
            item.addToCart();
        } else if (action == CartAction.MINUS) {
            item.removeFromCart();
        } else {
            throw new RuntimeException("Unknown action '" + action + "'");
        }
        return item;
    }

    private Page<Item> find(PageRequest pageRequest) {
        return itemRepository.findAll(pageRequest);
    }

    private Page<Item> find(PageRequest pageRequest, String search) {
        return itemRepository.findByTitleContainingOrDescriptionContaining(
                pageRequest,
                search,
                search
        );
    }
}
