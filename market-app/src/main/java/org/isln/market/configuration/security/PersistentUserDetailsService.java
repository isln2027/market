package org.isln.market.configuration.security;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.model.MarketUser;
import org.isln.market.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersistentUserDetailsService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByLogin(username).map(PersistentUserDetailsService::fromMarketUser);
    }

    private static UserDetails fromMarketUser(MarketUser user) {
        return User.builder()
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[0]))
                .username(user.getLogin())
                .build();
    }
}
