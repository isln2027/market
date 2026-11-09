package org.isln.market.configuration.security;

import reactor.core.publisher.Mono;

import org.isln.market.model.MarketUser;

public interface ReactiveUserCreationService {
    Mono<MarketUser> createUser(String username, String rawPassword);
}
