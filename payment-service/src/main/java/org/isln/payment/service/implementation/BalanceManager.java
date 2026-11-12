package org.isln.payment.service.implementation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Mono;

import org.isln.payment.exception.InsufficientFundsException;
import org.isln.payment.service.AdminService;
import org.isln.payment.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class BalanceManager implements AdminService, PaymentService {
    private final Map<String, AtomicLong> balances = new ConcurrentHashMap<> ();

    public Mono<Long> getBalance(String username) {
        return Mono.fromCallable(() -> getUserBalance(username).get());
    }

    public Mono<Void> pay(long amount, String username) {
        return Mono.fromRunnable(
                () -> {
                    long current, next;
                    AtomicLong balance = getUserBalance(username);
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

    public Mono<Void> setBalance(long value, String username) {
        if (value < 0) {
            return Mono.error(() -> new IllegalArgumentException("Нельзя установить отрицательный баланс"));
        }
        return Mono.fromRunnable(() -> getUserBalance(username).set(value));
    }

    private AtomicLong getUserBalance(String username) {
        return balances.computeIfAbsent(username, u -> new AtomicLong(5000L));
    }
}
