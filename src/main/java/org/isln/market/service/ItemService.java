package org.isln.market.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.SortType;
import org.isln.market.model.Item;
import org.isln.market.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
