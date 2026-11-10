package org.isln.market.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.dto.UserDto;
import org.isln.market.model.MarketUser;
import org.isln.market.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationTrustResolver trustResolver;

    public Mono<MarketUser> createUser(String username, String rawPassword) {
        return userRepository.save(new MarketUser().setLogin(username).setPassword(passwordEncoder.encode(rawPassword)));
    }

    public Mono<UserDto> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> {
                    Authentication authentication = ctx.getAuthentication();
                    return fromAuth(authentication);
                });
    }

    private UserDto fromAuth(Authentication auth) {
        if (auth == null) {
            return UserDto.anonymous();
        }
        return new UserDto()
                .setName(auth.getName())
                .setAnonymous(trustResolver.isAnonymous(auth));
    }
}
