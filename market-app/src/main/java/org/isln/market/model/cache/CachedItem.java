package org.isln.market.model.cache;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.isln.market.model.Item;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Accessors(chain = true)
@RedisHash()
@NoArgsConstructor
public class CachedItem {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Long price;

    public CachedItem(Item item) {
        this.id = item.id();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.imgPath = item.getImgPath();
        this.price = item.getPrice();
    }
}
