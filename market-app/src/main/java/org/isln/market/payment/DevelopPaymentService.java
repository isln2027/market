package org.isln.market.payment;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!production")
public class DevelopPaymentService implements PaymentService {
    @Override
    public Mono<Boolean> canPay(long amount) {
        return Mono.just(Boolean.TRUE);
    }

    @Override
    public Mono<Void> pay(long amount) {
        return Mono.empty();
    }
}
