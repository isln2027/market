package org.isln.market.client;

import reactor.core.publisher.Mono;

import org.isln.payment.api.PaymentApi;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!production")
public class DevelopPaymentClient implements PaymentApi {
    @Override
    public Mono<Boolean> canPay(long amount) {
        boolean can = false;
        System.out.println("DEV payment: returning " + can);
        return Mono.just(can);
    }

    @Override
    public Mono<Void> pay(long amount) {
        return Mono.empty();
    }
}
