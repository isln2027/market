package org.isln.market.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.CartAction;
import org.isln.market.dto.SortType;
import org.isln.market.model.Cart;
import org.isln.market.model.Item;
import org.isln.market.model.ItemPage;
import org.isln.market.payment.PaymentService;
import org.isln.market.repository.ItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CachedItemDataAccessService cachedItemService;
    private final PaymentService paymentApi;

    private static final Map<SortType, Sort> SORT = Map.of(
            SortType.NO, Sort.unsorted(),
            SortType.ALPHA, Sort.by(Sort.Order.asc("title")),
            SortType.PRICE, Sort.by(Sort.Order.asc("price"))
    );

    @CacheEvict(value = {"items"}, allEntries = true)
    public Mono<Item> create(Item item) {
        return itemRepository.save(item);
    }

    public Mono<ItemPage> find(ItemRequestParameters parameters) {
        return cachedItemService.find(parameters)
                .flatMap(
                        page -> itemRepository.findCountByIdIn(page.getIds())
                                .collectMap(Item::getId, Item::getCount)
                                .map(counts -> new ItemPage(page, counts))
                );
    }

    public Mono<Item> findById(Long id) {
        return Mono.zip(cachedItemService.findById(id), itemRepository.findCountById(id))
                .map(t -> new Item(t.getT1(), t.getT2()));
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
}
