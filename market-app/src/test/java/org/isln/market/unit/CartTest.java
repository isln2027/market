package org.isln.market.unit;

import java.util.List;

import org.isln.market.model.Cart;
import org.isln.market.model.Item;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartTest {
    @Test
    public void totalPriceCountTest() {
        List<Item> items = List.of(
                new Item().setPrice(100L).setCount(1).setTitle("Item 1"),
                new Item().setPrice(200L).setCount(2).setTitle("Item 2"),
                new Item().setPrice(300L).setCount(3).setTitle("Item 3"),
                new Item().setPrice(100L).setCount(0).setTitle("Item 1")
        );
        Cart cart = new Cart(items);

        Long total = cart.getTotalPrice();

        assertThat(total).isEqualTo(100 + 200 * 2 + 300 * 3);
    }

    @Test
    public void totalPriceDoesNotFailIfNoPriceProvidedTest() {
        List<Item> items = List.of(
                new Item().setPrice(100L).setCount(1).setTitle("Item 1"),
                new Item().setCount(2).setTitle("Item 1")
        );
        Cart cart = new Cart(items);

        Long total = cart.getTotalPrice();

        assertThat(total).isEqualTo(100);
    }

    @Test
    public void itemIsAddedToCartTest() {
        int count = 2;
        Item item = new Item().setCount(count);

        item.addToCart();

        assertThat(item.getCount()).isEqualTo(count + 1);
    }

    @Test
    public void itemIsRemovedFromCartIfPresentInCartTest() {
        int count = 2;
        Item item = new Item().setCount(count);

        item.removeFromCart();

        assertThat(item.getCount()).isEqualTo(count - 1);
    }

    @Test
    public void itemCountStaysNonNegative() {
        int count = 0;
        Item item = new Item().setCount(count);

        item.removeFromCart();

        assertThat(item.getCount()).isNotNegative();
    }
}
