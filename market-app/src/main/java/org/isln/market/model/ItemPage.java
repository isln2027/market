package org.isln.market.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.isln.market.model.cache.CachedItem;
import org.isln.market.model.cache.CachedItemPage;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ItemPage {
    private List<Item> content;
    private Boolean hasPrevious;
    private Boolean hasNext;

    public ItemPage(CachedItemPage page, Map<Long, Integer> counts) {
        int size = counts.size();
        List<Item> items = new ArrayList<>(size);
        List<CachedItem> cachedItems = page.getContent();
        System.out.println(page);
        System.out.println(page.getContent() + "");
        for (int i = 0; i < size; i++) {
            CachedItem cachedItem = cachedItems.get(i);
            items.add(new Item(cachedItem, counts.get(cachedItem.getId())));
        }
        this.content = items;
        this.hasNext = page.getHasNext();
        this.hasPrevious = page.getHasPrevious();
    }
}
