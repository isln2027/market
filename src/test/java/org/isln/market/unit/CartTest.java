package org.isln.market.unit;

import org.isln.market.model.Item;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartTest {
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
