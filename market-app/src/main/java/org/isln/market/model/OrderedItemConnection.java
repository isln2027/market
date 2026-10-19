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
@Table("order_item_connection")
public class OrderedItemConnection {
    @Id
    private Long id;
    @Column("order_id")
    private Long orderId;
    @Column("item_id")
    private Long itemId;
    @Column("item_count")
    private Integer itemCount;
}
