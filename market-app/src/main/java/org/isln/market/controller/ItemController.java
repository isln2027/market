package org.isln.market.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.controller.request.CartActionRequest;
import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.Paging;
import org.isln.market.model.Item;
import org.isln.market.service.ItemModelAdapter;
import org.isln.market.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @Value("${application.view.item.max-items-in-row}")
    private Integer maxItemsInRow;

    @GetMapping({"/items", "/"})
    public Mono<Rendering> findItems(ItemRequestParameters parameters) {
        return itemService.find(parameters).map(
                page -> Rendering.view("items").model(getAttributes(parameters, page)).build()
        );
    }

    @GetMapping({"/items/{id}"})
    public Mono<Rendering> findItem(@PathVariable Long id) {
        return itemService.findById(id).map(item -> Rendering.view("item").modelAttribute("item", item).build());
    }

    @PostMapping({"/items"})
    public Mono<Rendering> performCartAction(CartActionRequest parameters) {
        return itemService.performAction(parameters.getId(), parameters.getAction())
                .map(item -> Rendering.redirectTo(createUrl(parameters)).build());
    }

    private static String createUrl(CartActionRequest parameters) {
        return "/items?sort=%s&search=%s&pageNumber=%s&pageSize=%s".formatted(
                parameters.getSort(),
                parameters.getSearch() == null ? "" : parameters.getSearch(),
                parameters.getPageNumber(),
                parameters.getPageSize()
        );
    }

    private Map<String, Object> getAttributes(ItemRequestParameters parameters, Page<Item> page) {
        Map<String, Object> attributes = new HashMap<>();
        List<List<Item>> items = ItemModelAdapter.putItemsInRows(page.getContent(), maxItemsInRow);
        attributes.put("sort", parameters.getSort().name());
        attributes.put("search", parameters.getSearch());
        attributes.put("items", items);
        attributes.put("paging",
                new Paging(
                        parameters.getPageSize(),
                        parameters.getPageNumber(),
                        page.hasPrevious(),
                        page.hasNext()
                )
        );
        return attributes;
    }
}
