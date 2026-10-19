package org.isln.market.integration;

import java.util.List;

import reactor.core.publisher.Mono;

import org.isln.market.controller.CartController;
import org.isln.market.controller.OrderController;
import org.isln.market.controller.OrderRequest;
import org.isln.market.model.Cart;
import org.isln.market.model.Item;
import org.isln.market.model.Order;
import org.isln.market.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.isln.market.integration.ItemFactory.getItems;

@Testcontainers
@ImportTestcontainers(PostgresContainer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
public class OrderTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartController cartController;
    @Autowired
    private OrderController orderController;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void orderCreatedTest() throws Exception {
        List<Item> items = getItems(3);

        Item first = items.getFirst();
        Item second = items.get(1);
        List<Item> savedItems = Mono.zip(
                        itemRepository.save(first),
                        itemRepository.save(second),
                        itemRepository.save(items.get(2))
                )
                .map(t -> List.of(t.getT1(), t.getT2(), t.getT3()))
                .block();

        addToCart(items.getFirst());
        addToCart(items.getFirst());
        addToCart(items.get(1));

        ExchangeResult result = webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .returnResult();

        String location = result.getResponseHeaders().get("Location").getFirst();
        UriComponents uri = UriComponentsBuilder.fromUriString(location).build();
        List<String> pathSegments = uri.getPathSegments();
        assertThat(pathSegments.getFirst()).isEqualTo("orders");
        assertThat(uri.getQueryParams().asSingleValueMap().get("newOrder")).isEqualTo("true");
        Long id = Long.parseLong(pathSegments.get(1));
        Order order = orderController.findById(id, new OrderRequest())
                .map(rendering -> (Order) rendering.modelAttributes().get("order")).block();
        assertThat(order).isNotNull();
        assertThat(order.getItems()).hasSize(2);
        Cart expectedCart = new Cart(List.of(first.setCount(2), second.setCount(1)));
        assertThat(order.totalSum()).isEqualTo(expectedCart.getTotalPrice());
        Cart cart = (Cart) cartController.getCart().block().modelAttributes().get("items");
        assertThat(cart).isEmpty();
    }

    private void addToCart(Item item) {
        webTestClient.post()
                .uri("/cart/items")
                .body(BodyInserters.fromFormData("id", item.getId().toString()).with("action", "PLUS"))
                .exchange()
                .returnResult();
    }
}
