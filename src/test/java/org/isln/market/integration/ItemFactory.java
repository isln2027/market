package org.isln.market.integration;

import java.util.List;
import java.util.stream.Stream;

import org.isln.market.model.Item;
import org.jetbrains.annotations.NotNull;

public class ItemFactory {
    public static @NotNull List<Item> getItems(int count) {
        return Stream
                .iterate(1L, i -> i + 1)
                .limit(count)
                .map(
                        i -> new Item()
                                .setTitle("Item " + i)
                                .setDescription("Description " + i)
                                .setPrice(Math.round(Math.random() * 1000))
                )
                .toList();
    }
}
