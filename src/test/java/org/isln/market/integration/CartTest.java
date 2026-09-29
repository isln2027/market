package org.isln.market.integration;

import java.util.List;

import org.isln.market.controller.CartController;
import org.isln.market.controller.OrderController;
import org.isln.market.dto.CartAction;
import org.isln.market.model.Cart;
import org.isln.market.model.Item;
import org.isln.market.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.isln.market.integration.ItemFactory.getItems;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresContainer.class)
public class CartTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderController orderController;
    @Autowired
    private CartController cartController;

    @Test
    public void cartCreatedTest() {
        List<Item> items = getItems(5);
        itemRepository.saveAll(items);
        Item first = items.getFirst();
        cartController.performAction(first.getId(), CartAction.PLUS);
        cartController.performAction(first.getId(), CartAction.PLUS);
        Item second = items.get(1);
        cartController.performAction(second.getId(), CartAction.PLUS);
        Model model = new ExtendedModelMap();

        cartController.getCart(model);

        Cart cart = (Cart) model.getAttribute("items");
        assertThat(cart).hasSize(2);
        assertThat((Long) model.getAttribute("total")).isEqualTo(cart.getTotalPrice());
    }
}
