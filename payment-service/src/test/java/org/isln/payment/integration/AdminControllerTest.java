package org.isln.payment.integration;

import java.util.Map;

import org.isln.payment.controller.AdminController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AdminControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        webTestClient.post()
                .uri("/admin/user")
                .body(user())
                .exchange()
                .returnResult();
    }

    @Test
    public void balanceIsUpdatedTest() {
        long balance = 12345L;
        String user = "user";
        webTestClient.post()
                .uri("/admin/balance")
                .body(withBalance(balance, user))
                .exchange()
                .returnResult();

        Long newBalance = (Long) adminController.showBalance(user).block().modelAttributes().get("balance");
        assertThat(newBalance).isEqualTo(balance);
    }

    @Test
    public void balanceForUserIsUpdatedTest() {
        long balance = 12345L;
        String user = "user";
        webTestClient.post()
                .uri("/admin/balance")
                .body(withBalance(balance, user))
                .exchange()
                .returnResult();
        webTestClient.put()
                .uri("/admin/balance")
                .body(withBalance(10, "another"))
                .exchange()
                .returnResult();

        Long newBalance = (Long) adminController.showBalance(user).block().modelAttributes().get("balance");
        assertThat(newBalance).isEqualTo(balance);
    }

    @Test
    public void balanceIsNotUpdatedOnInvalidInputTest() {
        long balance = -12345L;
        String user = "user";
        Long oldBalance = (Long) adminController.showBalance(user).block().modelAttributes().get("balance");

        webTestClient.post()
                .uri("/admin/balance")
                .body(withBalance(balance, user))
                .exchange()
                .expectStatus()
                .isBadRequest();

        Long newBalance = (Long) adminController.showBalance(user).block().modelAttributes().get("balance");
        assertThat(newBalance).isEqualTo(oldBalance);
    }

    private static BodyInserters.@NotNull FormInserter<String> withBalance(long balance, String user) {
        return BodyInserters.fromFormData(
                MultiValueMap.fromSingleValue(
                        Map.of(
                                "balance", Long.toString(balance),
                                "username", user
                        )
                )
        );
    }

    private static BodyInserters.@NotNull FormInserter<String> user() {
        return BodyInserters.fromFormData(MultiValueMap.fromSingleValue(Map.of("username", "user")));
    }
}
