package org.isln.market.model.cache;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Accessors(chain = true)
public class CachedItemPage {
    private List<CachedItem> content;
    private Boolean hasPrevious;
    private Boolean hasNext;

    @JsonIgnore
    public List<Long> getIds() {
        return content.stream().map(CachedItem::getId).toList();
    }
}
