package org.isln.payment.integration;

import java.util.Map;

import org.isln.payment.controller.AdminController;
import org.isln.payment.model.MoneyContainer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PaymentControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AdminController adminController;
    @Autowired
    private ApplicationContext context;

    @BeforeEach
    @WithMockUser(value = "admin", authorities = "ADMIN")
    void setUp() {
        this.webTestClient = WebTestClient
                .bindToApplicationContext(context)
                .apply(SecurityMockServerConfigurers.springSecurity())
                .configureClient()
                .build();
        webTestClient.post()
                .uri("/admin/user")
                .body(user())
                .exchange()
                .returnResult();
    }

    @Test
    @WithMockUser(value = "admin", authorities = "ADMIN")
    public void getBalanceTest() {
        long balance = 10;
        webTestClient.post()
                .uri("/admin/balance")
                .body(withBalance(balance))
                .exchange()
                .returnResult();

        clientWithMockJwt()
                .post()
                .uri("/balance")
                .bodyValue(Map.of("username", "user"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MoneyContainer.class)
                .value(container -> {
                    assertThat(container).isNotNull();
                    assertThat(container.getAmount()).isEqualTo(balance);
                });
    }

    @Test
    @WithMockUser(value = "admin", authorities = "ADMIN")
    public void amountIsPayedTest() {
        long balance = 10;
        webTestClient.post()
                .uri("/admin/balance")
                .body(withBalance(balance))
                .exchange()
                .returnResult();

        clientWithMockJwt().post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new MoneyContainer().amount(3L).username("user"))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult();

        clientWithMockJwt().post()
                .uri("/balance")
                .bodyValue(Map.of("username", "user"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MoneyContainer.class)
                .value(container -> {
                    assertThat(container).isNotNull();
                    assertThat(container.getAmount()).isEqualTo(7);
                });
    }

    @Test
    @WithMockUser(value = "admin", authorities = "ADMIN")
    public void amountIsNotPayedIfInsufficientFundsTest() {
        long balance = 10;
        webTestClient.post()
                .uri("/admin/balance")
                .body(withBalance(balance))
                .exchange()
                .returnResult();

        clientWithMockJwt().post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new MoneyContainer().amount(20L).username("user"))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                .returnResult();

        Long newBalance = (Long) adminController.showBalance("user").block().modelAttributes().get("balance");
        assertThat(newBalance).isEqualTo(10);
    }

    private static BodyInserters.@NotNull FormInserter<String> withBalance(long balance) {
        return BodyInserters.fromFormData(
                MultiValueMap.fromSingleValue(
                        Map.of(
                                "balance", Long.toString(balance),
                                "username", "user"
                        )
                )
        );
    }

    private static BodyInserters.@NotNull FormInserter<String> user() {
        return BodyInserters.fromFormData(MultiValueMap.fromSingleValue(Map.of("username", "user")));
    }

    private @NotNull WebTestClient clientWithMockJwt() {
        return webTestClient.mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("MARKET_APP")));
    }
}
