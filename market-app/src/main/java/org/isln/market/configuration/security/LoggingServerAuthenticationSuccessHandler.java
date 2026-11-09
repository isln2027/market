package org.isln.market.configuration.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class LoggingServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final ServerAuthenticationSuccessHandler handler;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info("User '{}' authenticated successfully", authentication.getName());
        return handler.onAuthenticationSuccess(webFilterExchange, authentication);
    }
}
