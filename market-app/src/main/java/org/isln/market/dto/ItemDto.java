package org.isln.market.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {
    private String title;
    private String description;
    private String imgPath;
    private Long price;
}
