package org.isln.market.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ItemPage {
    private List<Item> content;
    private Boolean hasPrevious;
    private Boolean hasNext;
}
