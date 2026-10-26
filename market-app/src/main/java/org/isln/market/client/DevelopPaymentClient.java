package org.isln.market.client;

import reactor.core.publisher.Mono;

import org.isln.payment.api.PaymentApi;
import org.isln.payment.api.PaymentResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!production")
public class DevelopPaymentClient implements PaymentApi {
    @Override
    public Mono<Boolean> canPay(long amount) {
        return Mono.just(true);
    }

    @Override
    public Mono<PaymentResult> pay(long amount) {
        return Mono.just(PaymentResult.SUCCESS);
    }
}
