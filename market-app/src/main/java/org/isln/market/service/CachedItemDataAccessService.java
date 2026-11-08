package org.isln.market.service;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.SortType;
import org.isln.market.model.cache.CachedItem;
import org.isln.market.model.cache.CachedItemPage;
import org.isln.market.repository.ItemRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CachedItemDataAccessService {
    private final ItemRepository itemRepository;
    private static final Map<SortType, Sort> SORT = Map.of(
            SortType.NO, Sort.unsorted(),
            SortType.ALPHA, Sort.by(Sort.Order.asc("title")),
            SortType.PRICE, Sort.by(Sort.Order.asc("price"))
    );

    @Cacheable(value = "item", key = "#id")
    public Mono<CachedItem> findById(Long id) {
        return itemRepository.findById(id).map(CachedItem::new);
    }

    @Cacheable(value = "items", key = "#parameters.toString()")
    public Mono<CachedItemPage> find(ItemRequestParameters parameters) {
        SortType sortType = parameters.getSort();
        if (!SORT.containsKey(sortType)) {
            throw new IllegalArgumentException("Unknown sort type '" + sortType + "'");
        }
        Sort sort = SORT.get(sortType);
        String search = parameters.getSearch();
        PageRequest pageRequest = PageRequest.of(parameters.getPageNumber() - 1, parameters.getPageSize(), sort);
        return search == null ? find(pageRequest) : find(pageRequest, search);
    }

    private Mono<CachedItemPage> find(PageRequest pageRequest) {
        return Mono.zip(
                        itemRepository.findBy(pageRequest).map(CachedItem::new).collectList(),
                        itemRepository.count()
                )
                .map(tuple -> map(pageRequest, tuple));
    }

    private Mono<CachedItemPage> find(PageRequest pageRequest, String search) {
        return Mono.zip(
                        itemRepository
                                .findByTitleContainingOrDescriptionContaining(search, search, pageRequest)
                                .map(CachedItem::new)
                                .collectList(),
                        itemRepository.countByTitleContainingOrDescriptionContaining(search, search)
                )
                .map(tuple -> map(pageRequest, tuple));
    }

    private static CachedItemPage map(PageRequest pageRequest, Tuple2<List<CachedItem>, Long> tuple) {
        return new CachedItemPage()
                .setContent(tuple.getT1())
                .setHasPrevious(pageRequest.getPageNumber() > 0)
                .setHasNext(
                        hasNext(
                                tuple.getT2(),
                                pageRequest.getPageNumber(),
                                pageRequest.getPageSize()
                        )
                );
    }

    private static Boolean hasNext(long total, int pageNumber, int pageSize) {
        return Math.ceilDiv(total, pageSize) > pageNumber + 1;
    }
}
