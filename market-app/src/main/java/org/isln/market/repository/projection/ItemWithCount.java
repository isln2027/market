package org.isln.market.repository.projection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ItemWithCount {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Long price;
    private Integer itemCount;
}
