package org.isln.market.controller.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.isln.market.dto.Sort;
import org.isln.market.repository.ItemRepository;

@Getter
@Setter
@Accessors(chain = true)
public class ItemRequestParameters {
    private String search;
    private Sort sort = Sort.NO;
    private Integer pageNumber = 1;
    private Integer pageSize = 5;
}
