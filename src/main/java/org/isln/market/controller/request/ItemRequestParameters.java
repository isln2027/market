package org.isln.market.controller.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.isln.market.dto.SortType;

@Getter
@Setter
@Accessors(chain = true)
public class ItemRequestParameters {
    private String search;
    private SortType sort = SortType.NO;
    private Integer pageNumber = 1;
    private Integer pageSize = 5;
}
