package org.isln.market.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Accessors(chain = true)
@Table(name = "orders")
public class Order {
    @Id
    private Long id;

    @Transient
    private List<Item> items;

    public Long totalSum() {
        return new Cart(items).getTotalPrice();
    }

    public Long id() {
        return id;
    }

    public List<Item> items() {
        return items;
    }
}

