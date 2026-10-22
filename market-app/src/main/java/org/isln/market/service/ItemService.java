package org.isln.market.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.CartAction;
import org.isln.market.dto.SortType;
import org.isln.market.model.Cart;
import org.isln.market.model.Item;
import org.isln.market.repository.ItemRepository;
import org.isln.payment.api.PaymentApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final PaymentApi paymentApi;

    private static final Map<SortType, Sort> SORT = Map.of(
            SortType.NO, Sort.unsorted(),
            SortType.ALPHA, Sort.by(Sort.Order.asc("title")),
            SortType.PRICE, Sort.by(Sort.Order.asc("price"))
    );

    public Mono<Page<Item>> find(ItemRequestParameters parameters) {
        SortType sortType = parameters.getSort();
        if (!SORT.containsKey(sortType)) {
            throw new IllegalArgumentException("Unknown sort type '" + sortType + "'");
        }
        Sort sort = SORT.get(sortType);
        String search = parameters.getSearch();
        PageRequest pageRequest = PageRequest.of(parameters.getPageNumber() - 1, parameters.getPageSize(), sort);
        return search == null ? find(pageRequest) : find(pageRequest, search);
    }

    public Mono<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

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

    public Mono<Cart> getCart() {
        return itemRepository.findByCountGreaterThan(0)
                .collectList()
                .map(Cart::new)
                .flatMap(c -> paymentApi.canPay(c.getTotalPrice()).map(c::setCanBuy));
    }

    public Mono<Void> cleanupCart() {
        return itemRepository.saveAll(itemRepository.findAll().map(item -> item.setCount(0))).then();
    }

    private Mono<Page<Item>> find(PageRequest pageRequest) {
        return Mono.zip(
                itemRepository.findBy(pageRequest).collectList(),
                itemRepository.count()
        ).map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
    }

    private Mono<Page<Item>> find(PageRequest pageRequest, String search) {
        return Mono.zip(
                        itemRepository
                                .findByTitleContainingOrDescriptionContaining(search, search, pageRequest)
                                .collectList(),
                        itemRepository.countByTitleContainingOrDescriptionContaining(search, search)
                )
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageRequest, tuple.getT2()));
    }
}
