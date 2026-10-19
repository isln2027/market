package org.isln.market.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@Setter
@Accessors(chain = true)
@Table(name = "items")
public class Item {
    @Id
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Long price;
    private Integer count = 0;

    public void addToCart() {
        this.setCount(count + 1);
    }

    public void removeFromCart() {
        if (count > 0) {
            count--;
        }
    }

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
}


