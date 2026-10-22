package org.isln.payment.service;

import reactor.core.publisher.Mono;

public interface AdminService {
    Mono<Long> getBalance();

    Mono<Void> setBalance(long balance);
}
