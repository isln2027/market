package org.isln.market.configuration.security;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.model.MarketUser;
import org.isln.market.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@RequiredArgsConstructor
public class OAuthUserService implements ReactiveOAuth2UserService<OidcUserRequest, OidcUser> {
    private final OidcReactiveOAuth2UserService delegate;
    private final UserService userService;

    @Override
    public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {
        return delegate.loadUser(userRequest)
                .flatMap(oidcUser -> {
                    return userService.findByLogin(oidcUser.getName())
                            .switchIfEmpty(
                                    userService.createUser(oidcUser.getName(), UUID.randomUUID().toString())
                            )
                            .flatMap(appUser -> addRoles(oidcUser, appUser));
                });
    }

    private Mono<OidcUser> addRoles(OidcUser oidcUser, MarketUser marketUser) {
        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        marketUser.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        return Mono.just(new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo()));
    }
}