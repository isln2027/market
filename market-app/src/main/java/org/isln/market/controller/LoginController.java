package org.isln.market.controller;

import java.util.Map;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
public class LoginController {
    @GetMapping("/login")
    public Mono<Rendering> login(@RequestParam Map<String, String> parameters) {
        return Mono.just(
                Rendering.view("login")
                        .modelAttribute("error", parameters.containsKey("error"))
                        .modelAttribute("logout", parameters.containsKey("logout"))
                        .modelAttribute("register", parameters.containsKey("register"))
                        .build()
        );
    }

    @GetMapping("/logout")
    public Mono<Rendering> logout() {
        // todo implement proper logout
        return Mono.just(Rendering.view("logout").build());
    }
}