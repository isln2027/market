package org.isln.market.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

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

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @Value("${application.view.item.max-items-in-row}")
    private Integer maxItemsInRow;

    @GetMapping({"/items", "/"})
    public String findItems(ItemRequestParameters parameters, Model model) {
        Page<Item> page = itemService.find(parameters);
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
        return "items";
    }
}
