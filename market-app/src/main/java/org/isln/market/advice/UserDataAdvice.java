package org.isln.market.advice;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.dto.UserDto;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;

@ControllerAdvice
@RequiredArgsConstructor
public class UserDataAdvice {
    private final AuthenticationTrustResolver trustResolver;

    @ModelAttribute("currentUser")
    public Mono<UserDto> currentUser(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .map(p -> (Authentication) p)
                .map(this::fromAuth);
    }

    private UserDto fromAuth(Authentication auth) {
        return new UserDto()
                .setName(auth.getName())
                .setAnonymous(trustResolver.isAnonymous(auth));
    }
}