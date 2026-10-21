package org.isln.payment.api;

import reactor.core.publisher.Mono;

public interface PaymentApi {
    Mono<Boolean> canPay(long amount);
    Mono<Void> pay(long amount);
}
