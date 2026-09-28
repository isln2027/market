package org.isln.market.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.isln.market.model.Item;

public class ItemModelAdapter {
    public static List<List<Item>> putItemsInRows(List<Item> items, int itemsInRow) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<Item>> rows = new ArrayList<>();
        int i = 0;
        for (var item : items) {
            List<Item> currentRow;
            if (i == 0) {
                currentRow = new ArrayList<>();
                rows.add(currentRow);
            } else {
                currentRow = rows.getLast();
            }
            currentRow.add(item);
            if (i < itemsInRow - 1) {
                i++;
            } else {
                i = 0;
            }
        }
        List<Item> lastRow = rows.getLast();
        int lastRowSize = lastRow.size();
        if (lastRowSize < itemsInRow) {
            Item dummy = new Item().setId(-1L);
            for (int j = 0; j < itemsInRow - lastRowSize; j++) {
                lastRow.add(dummy);
            }
        }
        return rows;
    }

    private ItemModelAdapter() {
        throw new UnsupportedOperationException();
    }
}
