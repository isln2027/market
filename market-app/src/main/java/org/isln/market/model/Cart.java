package org.isln.market.model;

import java.util.ArrayList;
import java.util.Collection;

public class Cart extends ArrayList<Item> {
    private boolean canBuy;

    public boolean canBuy() {
        return canBuy;
    }

    public Cart setCanBuy(boolean canBuy) {
        this.canBuy = canBuy;
        return this;
    }

    public Long getTotalPrice() {
        return this.stream()
                .filter(item -> item.getPrice() != null && item.getCount() != null)
                .mapToLong(item -> item.getCount() * item.getPrice())
                .sum();
    }

    public Cart(Collection<Item> items) {
        super(items);
    }
}
