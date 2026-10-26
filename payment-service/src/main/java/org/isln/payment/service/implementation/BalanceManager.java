package org.isln.payment.service.implementation;

import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Mono;

import org.isln.payment.api.PaymentResult;
import org.isln.payment.service.AdminService;
import org.isln.payment.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class BalanceManager implements AdminService, PaymentService {
    private final AtomicLong balance = new AtomicLong(5000L);

    public Mono<Long> getBalance() {
        return Mono.fromCallable(balance::get);
    }

    public Mono<PaymentResult> pay(long amount) {
        return Mono.fromCallable(
                () -> {
                    long current, next;
                    do {
                        current = balance.get();
                        if (current < amount) {
                            return PaymentResult.INSUFFICIENT_FUNDS;
                        }
                        next = current - amount;
                    } while (!balance.compareAndSet(current, next));
                    return PaymentResult.SUCCESS;
                }
        );
    }

    public Mono<Void> setBalance(long value) {
        if (value < 0) {
            return Mono.error(() -> new IllegalArgumentException("Нельзя установить отрицательный баланс"));
        }
        return Mono.fromRunnable(() -> balance.set(value));
    }
}
