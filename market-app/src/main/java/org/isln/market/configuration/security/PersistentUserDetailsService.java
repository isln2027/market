package org.isln.market.configuration.security;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.model.MarketUser;
import org.isln.market.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersistentUserDetailsService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByLogin(username).map(PersistentUserDetailsService::fromMarketUser);
    }

    private static UserDetails fromMarketUser(MarketUser user) {
        return User.builder()
                .password(user.getPassword())
                .roles(user.getRoles().stream().map(Role::name).toList().toArray(new String[0]))
                .username(user.getLogin())
                .build();
    }
}
