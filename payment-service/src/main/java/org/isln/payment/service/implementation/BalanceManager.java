package org.isln.payment.service.implementation;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Mono;

import org.isln.payment.exception.InsufficientFundsException;
import org.isln.payment.exception.UserNotFoundException;
import org.isln.payment.service.AdminService;
import org.isln.payment.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class BalanceManager implements AdminService, PaymentService {
    public static final AtomicLong DEFAULT_BALANCE = new AtomicLong(5000L);
    private final Map<String, AtomicLong> balances;

    public BalanceManager() {
        balances = new ConcurrentHashMap<>();
        balances.put("admin", DEFAULT_BALANCE);
    }

    @Override
    public Mono<Void> createUser(String username) {
        if (balances.containsKey(username)) {
            return Mono.error(new IllegalArgumentException("User already exists"));
        } else {
            return Mono.fromRunnable(() -> balances.put(username, DEFAULT_BALANCE));
        }
    }

    @Override
    public Mono<Long> getBalance(String username) {
        return getUserBalance(username).map(AtomicLong::get);
    }

    @Override
    public Mono<Void> pay(long amount, String username) {
        return getUserBalance(username).doOnNext(
                        balance -> {
                            long current, next;
                            do {
                                current = balance.get();
                                if (current < amount) {
                                    throw new InsufficientFundsException();
                                }
                                next = current - amount;
                            } while (!balance.compareAndSet(current, next));
                        }
                )
                .then();
    }

    @Override
    public Mono<Void> setBalance(long value, String username) {
        if (value < 0) {
            return Mono.error(() -> new IllegalArgumentException("Нельзя установить отрицательный баланс"));
        }
        return getUserBalance(username).doOnNext(b -> b.set(value)).then();
    }

    @Override
    public Mono<Collection<String>> getUsers() {
        return Mono.just(balances.keySet());
    }

    private Mono<AtomicLong> getUserBalance(String username) {
        if (balances.containsKey(username)) {
            return Mono.just(balances.get(username));
        } else {
            return Mono.error(new UserNotFoundException());
        }
    }
}
