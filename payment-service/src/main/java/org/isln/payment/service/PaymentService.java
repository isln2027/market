package org.isln.payment.service;

import reactor.core.publisher.Mono;

import org.isln.payment.api.PaymentResult;

public interface PaymentService {
    Mono<Long> getBalance();

    Mono<PaymentResult> pay(long amount);
}
