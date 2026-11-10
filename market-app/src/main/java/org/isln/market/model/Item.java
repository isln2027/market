package org.isln.market.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.isln.market.model.cache.CachedItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@Setter
@Accessors(chain = true)
@Table(name = "items")
@NoArgsConstructor
public class Item {
    @Id
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Long price;
    @Transient
    private Integer count = 0;

    public Long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String imgPath() {
        return imgPath;
    }

    public Long price() {
        return price;
    }

    public Integer count() {
        return count;
    }

    public Item(CachedItem item, Integer count) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.imgPath = item.getImgPath();
        this.price = item.getPrice();
        this.count = count;
    }

    public Item(CachedItem item) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.imgPath = item.getImgPath();
        this.price = item.getPrice();
    }
}


