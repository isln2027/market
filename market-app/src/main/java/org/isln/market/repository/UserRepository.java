package org.isln.market.repository;

import reactor.core.publisher.Mono;

import org.isln.market.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByLogin(String login);
}
