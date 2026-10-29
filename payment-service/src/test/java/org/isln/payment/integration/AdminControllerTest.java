package org.isln.payment.integration;

import org.isln.payment.controller.AdminController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AdminControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AdminController adminController;

    @Test
    public void balanceIsUpdatedTest() {
        long balance = 12345L;

        webTestClient.post()
                .uri("/admin/balance")
                .body(BodyInserters.fromFormData("balance", Long.toString(balance)))
                .exchange()
                .returnResult();

        Long newBalance = (Long) adminController.showBalance().block().modelAttributes().get("balance");
        assertThat(newBalance).isEqualTo(balance);
    }

    @Test
    public void balanceIsNotUpdatedOnInvalidInputTest() {
        long balance = -12345L;
        Long oldBalance = (Long) adminController.showBalance().block().modelAttributes().get("balance");

        webTestClient.post()
                .uri("/admin/balance")
                .body(BodyInserters.fromFormData("balance", Long.toString(balance)))
                .exchange()
                .expectStatus()
                .isBadRequest();

        Long newBalance = (Long) adminController.showBalance().block().modelAttributes().get("balance");
        assertThat(newBalance).isEqualTo(oldBalance);
    }
}
