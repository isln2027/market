package org.isln.market.payment;

import reactor.core.publisher.Mono;

public interface PaymentService {
    Mono<Boolean> canPay(long amount);
    Mono<Void> pay(long amount);
}
