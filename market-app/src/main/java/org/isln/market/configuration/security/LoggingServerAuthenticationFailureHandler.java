package org.isln.market.configuration.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;

@Slf4j
@RequiredArgsConstructor
public class LoggingServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    private final ServerAuthenticationFailureHandler handler;

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        log.info("Authentication failed", exception);
        return handler.onAuthenticationFailure(webFilterExchange, exception);
    }
}
