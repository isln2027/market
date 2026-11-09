package org.isln.market.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
@Slf4j
@RequiredArgsConstructor
public class LoggingServerLogoutSuccessHandler implements ServerLogoutSuccessHandler {
    private final ServerLogoutSuccessHandler handler;
    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        log.info("User '{}' logged out successfully", authentication.getName());
        return handler.onLogoutSuccess(exchange, authentication);
    }
}
