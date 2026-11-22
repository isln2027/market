package org.isln.market.repository;

import reactor.core.publisher.Mono;

import org.isln.market.model.MarketUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface UserRepository extends ReactiveCrudRepository<MarketUser, Long> {
    Mono<MarketUser> findByLogin(String login);
}
