package org.isln.market.integration;

import java.util.List;
import java.util.Map;

import reactor.core.publisher.Mono;

import org.isln.market.configuration.security.Roles;
import org.isln.market.controller.CartController;
import org.isln.market.controller.OrderController;
import org.isln.market.controller.request.CartActionRequest;
import org.isln.market.dto.CartAction;
import org.isln.market.model.Cart;
import org.isln.market.model.Item;
import org.isln.market.model.MarketUser;
import org.isln.market.payment.PaymentService;
import org.isln.market.repository.ItemRepository;
import org.isln.market.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.result.view.Rendering;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.isln.market.integration.ItemFactory.getItems;
import static org.mockito.ArgumentMatchers.anyLong;

@ActiveProfiles("test")
@Testcontainers
@ImportTestcontainers(PostgresContainer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
public class CartTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderController orderController;
    @Autowired
    private CartController cartController;
    @MockitoBean
    private PaymentService paymentService;

    @Test
    @WithMockUser(username = "user", roles = Roles.CLIENT)
    public void cartCreatedTest() {
        userRepository.deleteAll().then(userRepository.save(new MarketUser().setLogin("user").setPassword("abc"))).block();
        Mockito.when(paymentService.canPay(anyLong())).thenReturn(Mono.just(Boolean.TRUE));
        List<Item> items = getItems(3);
        Mono<Void> prepare = Mono.zip(
                        itemRepository.save(items.getFirst()),
                        itemRepository.save(items.get(1)),
                        itemRepository.save(items.get(2))
                )
                .flatMap(
                        t -> Mono.zip(
                                cartController.performAction(new CartActionRequest(t.getT1().id(), CartAction.PLUS)),
                                cartController.performAction(new CartActionRequest(t.getT1().id(), CartAction.PLUS))
                        )
                )
                .then();

        Rendering rendering = prepare.then(cartController.getCart()).block();

        assertThat(rendering).isNotNull();
        Map<String, Object> model = rendering.modelAttributes();
        Cart cart = (Cart) model.get("items");
        assertThat(cart).hasSize(2);
        assertThat(cart.canBuy()).isTrue();
        assertThat((Long) model.get("total")).isEqualTo(cart.getTotalPrice());
    }
}
