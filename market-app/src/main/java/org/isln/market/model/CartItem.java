package org.isln.market.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Accessors(chain = true)
@Table(name = "cart_items")
public class CartItem {
    @Id
    private Long id;
    @Column("user_login")
    private String user;
    @Column("item_id")
    private Long itemId;
    @Column("item_count")
    private Integer count = 0;

    public void addToCart() {
        count++;
    }

    public void removeFromCart() {
        if (count > 0) {
            count--;
        }
    }
}
