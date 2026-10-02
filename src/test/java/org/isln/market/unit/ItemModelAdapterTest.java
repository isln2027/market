package org.isln.market.unit;

import java.util.List;
import java.util.stream.Stream;

import org.isln.market.model.Item;
import org.isln.market.service.ItemModelAdapter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemModelAdapterTest {
    @Test
    public void rowSizeIsCorrectTest() {
        List<Item> items = getItems(10);

        int itemsInRow = 3;
        List<List<Item>> arrangedItems = ItemModelAdapter.putItemsInRows(items, itemsInRow);

        assertThat(arrangedItems).allMatch(row -> row.size() == itemsInRow);
    }
    @Test
    public void correctDummyItemsAreUsedToTopUpLastRowTest() {
        List<Item> items = getItems(10);

        int itemsInRow = 3;
        List<List<Item>> arrangedItems = ItemModelAdapter.putItemsInRows(items, itemsInRow);

        assertThat(arrangedItems.getLast().get(1).getId()).isEqualTo(-1L);
        assertThat(arrangedItems.getLast().get(2).getId()).isEqualTo(-1L);
    }

    @Test
    public void noDummyItemsIfLasRowIsCorrectLengthTest() {
        List<Item> items = getItems(9);

        int itemsInRow = 3;
        List<List<Item>> arrangedItems = ItemModelAdapter.putItemsInRows(items, itemsInRow);

        assertThat(arrangedItems.getLast().getLast().getId()).isNotEqualTo(-1L);
    }

    private static @NotNull List<Item> getItems(int count) {
        return Stream
                .iterate(1L, i -> i + 1)
                .limit(count)
                .map(id -> new Item().setId(id))
                .toList();
    }
}
