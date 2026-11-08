package org.isln.payment.service;

import reactor.core.publisher.Mono;

public interface PaymentService {
    Mono<Long> getBalance();

    Mono<Void> pay(long amount);
}
