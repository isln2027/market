package org.isln.market.client;

import reactor.core.publisher.Mono;

import org.isln.payment.api.PaymentApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Profile("production")
public class PaymentClient implements PaymentApi {
    private final WebClient client;

    public PaymentClient(@Value("${payment.api.path}") String paymentApiPath) {
        this.client = WebClient.create(paymentApiPath);
    }

    @Override
    public Mono<Boolean> canPay(long amount) {
        return client.get()
                .uri("/can-pay?amount={amount}", amount)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    @Override
    public Mono<Void> pay(long amount) {
        return client.post()
                .uri("/pay?amount={amount}", amount)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
