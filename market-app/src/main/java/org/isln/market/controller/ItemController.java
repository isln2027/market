package org.isln.market.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.controller.mapper.Mapper;
import org.isln.market.controller.request.CartActionRequest;
import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.ItemDto;
import org.isln.market.dto.Paging;
import org.isln.market.model.Item;
import org.isln.market.model.ItemPage;
import org.isln.market.service.FileService;
import org.isln.market.service.ItemModelAdapter;
import org.isln.market.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final FileService fileService;
    private final Mapper mapper;

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

    @GetMapping({"/items/image"})
    public Mono<ResponseEntity<Resource>> findImage(@RequestParam String filename) {
        return fileService.getFile(filename)
                .map(ByteArrayResource::new)
                .map(
                        file -> ResponseEntity.ok()
                                .contentType(MediaTypeFactory.getMediaType(file).orElse(MediaType.APPLICATION_OCTET_STREAM))
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                        ContentDisposition.attachment()
                                                .filename(filename)
                                                .build()
                                                .toString())
                                .body(file)
                );
    }

    @PostMapping({"/items"})
    public Mono<Rendering> performCartAction(CartActionRequest parameters) {
        return itemService.performAction(parameters.getId(), parameters.getAction())
                .map(item -> Rendering.redirectTo(createUrl(parameters)).build());
    }

    @GetMapping("/items/management")
    public Mono<Rendering> getManagementPage() {
        return Mono.just(Rendering.view("management").modelAttribute("item", new ItemDto()).build());
    }

    @PostMapping("items/management")
    public Mono<Rendering> create(
            @ModelAttribute ItemDto item,
            @RequestPart("file") FilePart image) {
        return fileService.save(image)
                .map(filename -> mapper.map(item).setImgPath(filename))
                .flatMap(itemService::create)
                .then(Mono.just(Rendering.redirectTo("management").build()));
    }

    private static String createUrl(CartActionRequest parameters) {
        return "/items?sort=%s&search=%s&pageNumber=%s&pageSize=%s".formatted(
                parameters.getSort(),
                parameters.getSearch() == null ? "" : parameters.getSearch(),
                parameters.getPageNumber(),
                parameters.getPageSize()
        );
    }

    private Map<String, Object> getAttributes(ItemRequestParameters parameters, ItemPage page) {
        Map<String, Object> attributes = new HashMap<>();
        List<List<Item>> items = ItemModelAdapter.putItemsInRows(page.getContent(), maxItemsInRow);
        attributes.put("sort", parameters.getSort().name());
        attributes.put("search", parameters.getSearch());
        attributes.put("items", items);
        attributes.put("paging",
                new Paging(
                        parameters.getPageSize(),
                        parameters.getPageNumber(),
                        page.getHasPrevious(),
                        page.getHasNext()
                )
        );
        return attributes;
    }
}
