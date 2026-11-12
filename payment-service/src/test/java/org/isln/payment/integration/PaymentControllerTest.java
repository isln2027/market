package org.isln.payment.integration;

import java.util.Map;

import org.isln.payment.controller.AdminController;
import org.isln.payment.model.MoneyContainer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PaymentControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AdminController adminController;

    @Test
    public void getBalanceTest() {
        long balance = 10;
        webTestClient.put()
                .uri("/admin/balance")
                .body(withBalance(balance))
                .exchange()
                .returnResult();

        webTestClient.post()
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
    public void amountIsPayedTest() {
        long balance = 10;
        webTestClient.put()
                .uri("/admin/balance")
                .body(withBalance(balance))
                .exchange()
                .returnResult();

        ExchangeResult result = webTestClient.post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new MoneyContainer().amount(3L).username("user"))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult();

        webTestClient.post()
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
    public void amountIsNotPayedIfInsufficientFundsTest() {
        long balance = 10;
        webTestClient.put()
                .uri("/admin/balance")
                .body(withBalance(balance))
                .exchange()
                .returnResult();

        ExchangeResult result = webTestClient.post()
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
}
