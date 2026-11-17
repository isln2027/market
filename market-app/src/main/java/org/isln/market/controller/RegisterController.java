package org.isln.market.controller;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.controller.request.RegisterUserRequest;
import org.isln.market.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;

    @GetMapping("/register")
    public Mono<Rendering> register(@RequestParam(defaultValue = "false") Boolean error) {
        return Mono.just(Rendering.view("register").modelAttribute("error", error).build());
    }

    @PostMapping("/register")
    public Mono<Rendering> register(@ModelAttribute RegisterUserRequest request) {
        return userService.createUser(request.getUsername(), request.getPassword())
                .then(Mono.just(Rendering.redirectTo("/login?register").build()))
                .onErrorResume(ex -> Mono.just(Rendering.redirectTo("/register?error=true").build()));
    }
}