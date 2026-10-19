package org.isln.market.repository.projection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OrderedItem {
    private Long orderId;
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Long price;
    private Integer count = 0;
}
