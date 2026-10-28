package org.isln.market.controller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.model.Item;
import org.isln.market.model.ItemPage;
import org.isln.market.model.cache.CachedItem;
import org.isln.market.repository.ItemRepository;
import org.isln.market.service.CachedItemDataAccessService;
import org.isln.market.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.isln.market.dto.SortType.NO;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final CachedItemDataAccessService service;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @GetMapping("/test/{id}")
    public Mono<CachedItem> getItem(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/test")
    public Mono<ItemPage> getItems() {
        return itemService.find(new ItemRequestParameters().setPageSize(4).setSort(NO).setPageNumber(1));
    }

    @GetMapping("/test/repo")
    public Mono<Integer> getCount() {
        return itemRepository.findCountById(2L);
    }
}
