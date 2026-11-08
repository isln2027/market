package org.isln.market.dto;

public record Paging(
        Integer pageSize,
        Integer pageNumber,
        Boolean hasPrevious,
        Boolean hasNext
) {
}
