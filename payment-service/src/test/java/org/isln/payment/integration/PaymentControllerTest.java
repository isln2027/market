package org.isln.payment.integration;

import org.isln.payment.api.PaymentResult;
import org.isln.payment.controller.AdminController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PaymentControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AdminController adminController;

    @Test
    public void canPayTest() {
        long balance = 10;
        webTestClient.post()
                .uri("/admin/balance")
                .body(BodyInserters.fromFormData("balance", Long.toString(balance)))
                .exchange()
                .returnResult();

        ExchangeResult result = webTestClient.get()
                .uri("/can-pay?amount=5")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult();

        assertThat(new String(result.getResponseBodyContent())).isEqualTo(Boolean.TRUE.toString());
    }

    @Test
    public void cannotPayTest() {
        long balance = 10;
        webTestClient.post()
                .uri("/admin/balance")
                .body(BodyInserters.fromFormData("balance", Long.toString(balance)))
                .exchange()
                .returnResult();

        ExchangeResult result = webTestClient.get()
                .uri("/can-pay?amount=50")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult();

        assertThat(new String(result.getResponseBodyContent())).isEqualTo(Boolean.FALSE.toString());
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
                .uri("/pay?amount=3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PaymentResult.class).isEqualTo(PaymentResult.SUCCESS)
                .returnResult();

        Long newBalance = (Long) adminController.showBalance().block().modelAttributes().get("balance");
        assertThat(newBalance).isEqualTo(7);
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
                .uri("/pay?amount=30")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PaymentResult.class).isEqualTo(PaymentResult.INSUFFICIENT_FUNDS)
                .returnResult();

        Long newBalance = (Long) adminController.showBalance().block().modelAttributes().get("balance");
        assertThat(newBalance).isEqualTo(10);
    }
}
