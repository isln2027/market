package org.isln.payment.service.implementation;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.payment.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentManager {
    private final PaymentService paymentService;

    public Mono<Boolean> canPay(Long amount) {
        if (amount == null || amount < 0) {
            return Mono.error(() -> new IllegalArgumentException("Amount must be non-null and non-negative"));
        }
        return paymentService.getBalance().map(balance -> balance >= amount);
    }

    public Mono<Void> pay(Long amount) {
        if (amount == null || amount < 0) {
            return Mono.error(() -> new IllegalArgumentException("Amount must be non-null and non-negative"));
        }
        return paymentService.pay(amount);
    }
}

