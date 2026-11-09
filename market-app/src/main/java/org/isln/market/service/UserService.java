package org.isln.market.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.model.MarketUser;
import org.isln.market.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<MarketUser> createUser(String username, String rawPassword) {
        return userRepository.save(new MarketUser().setLogin(username).setPassword(passwordEncoder.encode(rawPassword)));
    }
}
