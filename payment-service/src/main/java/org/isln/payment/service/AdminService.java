package org.isln.payment.service;

import java.util.Collection;

import reactor.core.publisher.Mono;

public interface AdminService {
    Mono<Void> createUser(String username);

    Mono<Long> getBalance(String username);

    Mono<Void> setBalance(long balance, String username);

    Mono<Collection<String>> getUsers();
}
