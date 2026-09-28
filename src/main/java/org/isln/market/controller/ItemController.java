package org.isln.market.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.Paging;
import org.isln.market.model.Item;
import org.isln.market.service.ItemModelAdapter;
import org.isln.market.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
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
        List<List<Item>> items = ItemModelAdapter.putItemsInRows(itemService.find(parameters), maxItemsInRow);
        model.addAttribute("items", items);
        model.addAttribute("paging",
                new Paging(
                        parameters.getPageSize(),
                        parameters.getPageNumber(),
                        parameters.getPageNumber() > 1, // todo user service's response
                        true // todo implement
                        )
        );
        return "items";
    }
}
