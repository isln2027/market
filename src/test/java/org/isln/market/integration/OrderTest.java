package org.isln.market.integration;

import java.util.List;

import org.isln.market.controller.CartController;
import org.isln.market.controller.OrderController;
import org.isln.market.dto.CartAction;
import org.isln.market.model.Cart;
import org.isln.market.model.Item;
import org.isln.market.model.Order;
import org.isln.market.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.isln.market.integration.ItemFactory.getItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@ImportTestcontainers(PostgresContainer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class OrderTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartController cartController;
    @Autowired
    private OrderController orderController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void orderCreatedTest() throws Exception {
        List<Item> items = getItems(5);
        itemRepository.saveAll(items);
        Item first = items.getFirst();
        Item second = items.get(1);
        Cart cart = prepareCart(first, second);

        MvcResult result = mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        UriComponents uri = UriComponentsBuilder.fromUriString(location).build();
        List<String> pathSegments = uri.getPathSegments();
        assertThat(pathSegments.getFirst()).isEqualTo("orders");
        assertThat(uri.getQueryParams().asSingleValueMap().get("newOrder")).isEqualTo("true");
        Long id = Long.parseLong(pathSegments.get(1));
        Model model = new ExtendedModelMap();
        orderController.findById(id, false, model);
        Order order = (Order) model.getAttribute("order");
        assertThat(order).isNotNull();
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.totalSum()).isEqualTo(cart.getTotalPrice());
    }

    private Cart prepareCart(Item first, Item second) throws Exception {
        mockMvc.perform(post("/cart/items")
                .queryParam("id", first.getId().toString())
                .queryParam("action", CartAction.PLUS.name())
        );
        mockMvc.perform(post("/cart/items")
                .queryParam("id", first.getId().toString())
                .queryParam("action", CartAction.PLUS.name())
        );
        mockMvc.perform(post("/cart/items")
                .queryParam("id", second.getId().toString())
                .queryParam("action", CartAction.PLUS.name())
        );
        return new Cart(List.of(first.setCount(2), second.setCount(1)));
    }
}
