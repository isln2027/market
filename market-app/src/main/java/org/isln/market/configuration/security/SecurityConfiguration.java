package org.isln.market.configuration.security;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
public class SecurityConfiguration {
    private static final String[] PUBLIC_PATHS = new String[] {
            "/", "/login", "/items", "/items/image", "/items/*", "/item", "/static", "/register"
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ServerLogoutSuccessHandler serverLogoutSuccessHandler,
            ServerAuthenticationSuccessHandler authenticationSuccessHandler,
            ServerAuthenticationFailureHandler authenticationFailureHandler
    ) {
        http.csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/items/management").hasRole(Roles.ADMIN)
                        .pathMatchers(PUBLIC_PATHS).permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationSuccessHandler(authenticationSuccessHandler)
                        .authenticationFailureHandler(authenticationFailureHandler)
                )
                .anonymous(anonymous -> anonymous
                        .principal("guest")
                        .authorities(Roles.GUEST))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(serverLogoutSuccessHandler)
                )
                .oauth2Client(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSessionServerCsrfTokenRepository csrfTokenRepository() {
        return new WebSessionServerCsrfTokenRepository();
    }

    @Bean
    public ServerLogoutSuccessHandler redirectServerLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/login?logout"));
        return new LoggingServerLogoutSuccessHandler(logoutSuccessHandler);
    }

    @Bean
    public ServerAuthenticationSuccessHandler redirectServerLoginSuccessHandler() {
        return new LoggingServerAuthenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/"));
    }

    @Bean
    public ServerAuthenticationFailureHandler redirectServerLoginFailureHandler() {
        return new LoggingServerAuthenticationFailureHandler(
                new RedirectServerAuthenticationFailureHandler("/login?error")
        );
    }

    @Bean
    public AuthenticationTrustResolver getTrustResolver() {
        return new AuthenticationTrustResolverImpl();
    }
}
