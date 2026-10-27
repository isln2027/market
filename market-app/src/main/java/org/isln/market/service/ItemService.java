package org.isln.market.service;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.CartAction;
import org.isln.market.dto.SortType;
import org.isln.market.model.Cart;
import org.isln.market.model.Item;
import org.isln.market.model.ItemPage;
import org.isln.market.payment.PaymentService;
import org.isln.market.repository.ItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final PaymentService paymentApi;

    private static final Map<SortType, Sort> SORT = Map.of(
            SortType.NO, Sort.unsorted(),
            SortType.ALPHA, Sort.by(Sort.Order.asc("title")),
            SortType.PRICE, Sort.by(Sort.Order.asc("price"))
    );

    @CacheEvict(value = {"item", "items"})
    public Mono<Item> create(Item item) {
        return itemRepository.save(item);
    }

    @Cacheable(value = "items", key = "#parameters.toString()")
    public Mono<ItemPage> find(ItemRequestParameters parameters) {
        SortType sortType = parameters.getSort();
        if (!SORT.containsKey(sortType)) {
            throw new IllegalArgumentException("Unknown sort type '" + sortType + "'");
        }
        Sort sort = SORT.get(sortType);
        String search = parameters.getSearch();
        PageRequest pageRequest = PageRequest.of(parameters.getPageNumber() - 1, parameters.getPageSize(), sort);
        return search == null ? find(pageRequest) : find(pageRequest, search);
    }

    @Cacheable(value = "item", key = "#id")
    public Mono<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    @CacheEvict(value = {"item", "items", "cart"}, allEntries = true)
    @Transactional
    public Mono<Item> performAction(Long id, CartAction action) {
        return findById(id)
                .map(item -> {
                    if (action == CartAction.PLUS) {
                        item.addToCart();
                    } else if (action == CartAction.MINUS) {
                        item.removeFromCart();
                    } else if (action == CartAction.DELETE) {
                        item.setCount(0);
                    } else {
                        throw new RuntimeException("Unknown action '" + action + "'");
                    }
                    return item;
                })
                .flatMap(itemRepository::save);
    }

    @Cacheable(value = "cart")
    public Mono<Cart> getCart() {
        return itemRepository.findByCountGreaterThan(0)
                .collectList()
                .map(Cart::new)
                .flatMap(c -> paymentApi.canPay(c.getTotalPrice()).map(c::setCanBuy));
    }

    @CacheEvict(value = "cart")
    public Mono<Void> cleanupCart() {
        return itemRepository.saveAll(itemRepository.findAll().map(item -> item.setCount(0))).then();
    }

    private Mono<ItemPage> find(PageRequest pageRequest) {
        return Mono.zip(
                        itemRepository.findBy(pageRequest).collectList(),
                        itemRepository.count()
                )
                .map(tuple -> map(pageRequest, tuple));
    }

    private Mono<ItemPage> find(PageRequest pageRequest, String search) {
        return Mono.zip(
                        itemRepository
                                .findByTitleContainingOrDescriptionContaining(search, search, pageRequest)
                                .collectList(),
                        itemRepository.countByTitleContainingOrDescriptionContaining(search, search)
                )
                .map(tuple -> map(pageRequest, tuple));
    }

    private static ItemPage map(PageRequest pageRequest, Tuple2<List<Item>, Long> tuple) {
        return new ItemPage()
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
