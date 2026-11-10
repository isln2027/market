package org.isln.market.unit;

import org.isln.market.model.CartItem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartItemTest {
    @Test
    public void itemIsAddedToCartTest() {
        int count = 2;
        CartItem item = new CartItem().setCount(count);

        item.addToCart();

        assertThat(item.getCount()).isEqualTo(count + 1);
    }

    @Test
    public void itemIsRemovedFromCartIfPresentInCartTest() {
        int count = 2;
        CartItem item = new CartItem().setCount(count);

        item.removeFromCart();

        assertThat(item.getCount()).isEqualTo(count - 1);
    }

    @Test
    public void itemCountStaysNonNegative() {
        int count = 0;
        CartItem item = new CartItem().setCount(count);

        item.removeFromCart();

        assertThat(item.getCount()).isNotNegative();
    }
}
