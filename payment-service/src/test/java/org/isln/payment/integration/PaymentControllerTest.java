package org.isln.payment.integration;

import org.isln.payment.controller.AdminController;
import org.isln.payment.model.MoneyContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
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
        webTestClient.post()
                .uri("/admin/balance")
                .body(BodyInserters.fromFormData("balance", Long.toString(balance)))
                .exchange()
                .returnResult();

        webTestClient.get()
                .uri("/balance")
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
        webTestClient.post()
                .uri("/admin/balance")
                .body(BodyInserters.fromFormData("balance", Long.toString(balance)))
                .exchange()
                .returnResult();

        ExchangeResult result = webTestClient.post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new MoneyContainer().amount(3L))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult();

        webTestClient.get()
                .uri("/balance")
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
        webTestClient.post()
                .uri("/admin/balance")
                .body(BodyInserters.fromFormData("balance", Long.toString(balance)))
                .exchange()
                .returnResult();

        ExchangeResult result = webTestClient.post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new MoneyContainer().amount(20L))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                .returnResult();

        Long newBalance = (Long) adminController.showBalance().block().modelAttributes().get("balance");
        assertThat(newBalance).isEqualTo(10);
    }
}
