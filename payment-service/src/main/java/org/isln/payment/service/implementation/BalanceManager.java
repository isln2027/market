package org.isln.payment.service.implementation;

import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Mono;

import org.isln.payment.exception.InsufficientFundsException;
import org.isln.payment.service.AdminService;
import org.isln.payment.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class BalanceManager implements AdminService, PaymentService {
    private final AtomicLong balance = new AtomicLong(5000L);

    public Mono<Long> getBalance() {
        return Mono.fromCallable(balance::get);
    }

    public Mono<Void> pay(long amount) {
        return Mono.fromRunnable(
                () -> {
                    long current, next;
                    do {
                        current = balance.get();
                        if (current < amount) {
                           throw new InsufficientFundsException();
                        }
                        next = current - amount;
                    } while (!balance.compareAndSet(current, next));
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
