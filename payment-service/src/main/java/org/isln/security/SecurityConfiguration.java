package org.isln.security;

import java.util.Collection;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtAuthenticationConverter converter) {
        http
                .authorizeExchange(
                        exchanges -> exchanges
                                .pathMatchers("/admin/**").permitAll()
                                .anyExchange().hasAuthority("MARKET_APP")
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter))
                );
        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter authenticationConverter(Converter<Jwt, Flux<GrantedAuthority>> authorityConverter) {
        ReactiveJwtAuthenticationConverter authConverter = new ReactiveJwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(authorityConverter);
        return authConverter;
    }

    @Bean
    public Converter<Jwt, Flux<GrantedAuthority>> jwtAuthorityConverter() {
        return new LoggingJwtAuthorityConverter();
    }

    private static class LoggingJwtAuthorityConverter implements Converter<Jwt, Flux<GrantedAuthority>> {
        @Override
        public Flux<GrantedAuthority> convert(Jwt source) {
            Object access = source.getClaim("realm_access");
            if (access instanceof Map<?, ?> accessMap) {
                Object roles = accessMap.get("roles");
                if (roles instanceof Collection<?> roleCollection) {
                    Flux<GrantedAuthority> authorities = Flux.fromStream(
                            roleCollection.stream()
                                    .map(Object::toString)
                                    .map(r -> new GrantedAuthority() {
                                        @Override
                                        public @Nullable String getAuthority() {
                                            return r;
                                        }
                                    })
                    );
                    log.info("authorities={}", authorities);
                    return authorities;
                }
            }
            return Flux.empty();
        }
    }
}
