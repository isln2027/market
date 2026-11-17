package org.isln.payment.service;

import reactor.core.publisher.Mono;

public interface PaymentService {
    Mono<Long> getBalance(String username);

    Mono<Void> pay(long amount, String username);
}
