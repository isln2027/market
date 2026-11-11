package org.isln.market.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.controller.request.ItemRequestParameters;
import org.isln.market.dto.CartAction;
import org.isln.market.dto.UserDto;
import org.isln.market.model.Cart;
import org.isln.market.model.CartItem;
import org.isln.market.model.Item;
import org.isln.market.model.ItemPage;
import org.isln.market.model.cache.CachedItem;
import org.isln.market.model.cache.CachedItemPage;
import org.isln.market.payment.PaymentService;
import org.isln.market.repository.CartRepository;
import org.isln.market.repository.ItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final CachedItemDataAccessService cachedItemService;
    private final PaymentService paymentApi;

    @CacheEvict(value = {"items"}, allEntries = true)
    public Mono<Item> create(Item item) {
        return itemRepository.save(item);
    }

    public Mono<ItemPage> find(ItemRequestParameters parameters) {
        return Mono.zip(cachedItemService.find(parameters), userService.getCurrentUser())
                .flatMap(t -> getItemPage(t.getT1(), t.getT2()));
    }

    public Mono<Item> findById(Long id) {
        return Mono.zip(cachedItemService.findById(id), userService.getCurrentUser())
                .flatMap(t -> getItem(t.getT1(), t.getT2()));
    }

    @Transactional
    public Mono<Item> performAction(Long id, CartAction action) {
        return userService.getCurrentUser()
                .flatMap(
                        user -> cartRepository.findByUserAndItemId(user.getName(), id)
                                .defaultIfEmpty(new CartItem().setItemId(id).setUser(user.getName()))
                )
                .map(item -> {
                    performAction(action, item);
                    return item;
                })
                .flatMap(this::removeItemsWithZeroCount)
                .then(findById(id));
    }

    private Mono<Void> removeItemsWithZeroCount(CartItem cart) {
        if (cart.getCount() > 0) {
            return cartRepository.save(cart).then();
        } else {
            return cartRepository.delete(cart);
        }
    }

    public Mono<Cart> getCart() {
        return userService.getCurrentUser()
                .map(UserDto::getName)
                .flatMapMany(cartRepository::findCartItemsByUser)
                .map(Item::new)
                .collectList()
                .map(Cart::new)
                .flatMap(
                        c -> paymentApi.canPay(c.getTotalPrice())
                                .map(c::setCanBuy)
                                .onErrorResume(e -> Mono.just(c))
                );
    }

    public Mono<Void> cleanupCart() {
        return userService.getCurrentUser().map(UserDto::getName).flatMap(this::cleanupUserCart);
    }

    private Mono<ItemPage> getItemPage(CachedItemPage page, UserDto user) {
        if (user.isAnonymous()) {
            return Mono.just(new ItemPage(page));
        } else {
            return cartRepository.findCountByUserAndItemIdIn(user.getName(), page.getIds())
                    .collectMap(CartItem::getItemId, CartItem::getCount)
                    .map(counts -> new ItemPage(page, counts));
        }
    }

    private Mono<Item> getItem(CachedItem cachedItem, UserDto user) {
        if (user.isAnonymous()) {
            return Mono.just(new Item(cachedItem));
        } else {
            return cartRepository.findCountByUserAndItemId(user.getName(), cachedItem.getId())
                    .map(CartItem::getCount)
                    .defaultIfEmpty(0)
                    .map(count -> new Item(cachedItem, count));
        }
    }

    private Mono<Void> cleanupUserCart(String login) {
        return cartRepository.saveAll(cartRepository.findByUser(login).map(item -> item.setCount(0))).then();
    }

    private static void performAction(CartAction action, CartItem item) {
        if (action == CartAction.PLUS) {
            item.addToCart();
        } else if (action == CartAction.MINUS) {
            item.removeFromCart();
        } else if (action == CartAction.DELETE) {
            item.setCount(0);
        } else {
            throw new RuntimeException("Unknown action '" + action + "'");
        }
    }
}
