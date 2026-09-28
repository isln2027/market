package org.isln.market.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.market.controller.request.CartActionRequest;
import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.Paging;
import org.isln.market.model.Item;
import org.isln.market.service.ItemModelAdapter;
import org.isln.market.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @Value("${application.view.item.max-items-in-row}")
    private Integer maxItemsInRow;

    @GetMapping({"/items", "/"})
    public String findItems(ItemRequestParameters parameters, Model model) {
        Page<Item> page = itemService.find(parameters);
        fillModel(parameters, model, page);
        return "items";
    }

    @GetMapping({"/items/{id}"})
    public String findItem(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        return "item";
    }

    @PostMapping({"/items"})
    public String addToCart(CartActionRequest parameters, RedirectAttributes attributes) {
        itemService.performAction(parameters.getId(), parameters.getAction());
        attributes.addAttribute("sort", parameters.getSort().name());
        attributes.addAttribute("search", parameters.getSearch());
        attributes.addAttribute("pageNumber", parameters.getPageNumber());
        attributes.addAttribute("pageSize", parameters.getPageSize());
        return "redirect:/items";
    }

    private void fillModel(ItemRequestParameters parameters, Model model, Page<Item> page) {
        List<List<Item>> items = ItemModelAdapter.putItemsInRows(page.getContent(), maxItemsInRow);
        model.addAttribute("sort", parameters.getSort().name());
        model.addAttribute("search", parameters.getSearch());
        model.addAttribute("items", items);
        model.addAttribute("paging",
                new Paging(
                        parameters.getPageSize(),
                        parameters.getPageNumber(),
                        page.hasPrevious(),
                        page.hasNext()
                )
        );
    }
}
