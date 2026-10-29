package org.isln.payment.controller;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.payment.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
@RequestMapping("/admin/balance")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping
    public Mono<Rendering> showBalance() {
        return adminService.getBalance()
                .map(balance -> Rendering.view("balance").modelAttribute("balance", balance).build());
    }

    @PostMapping
    public Mono<Rendering> updateBalance(@ModelAttribute BalanceUpdateRequest balanceUpdateRequest) {
        return adminService.setBalance(balanceUpdateRequest.getBalance())
                .then(Mono.just(Rendering.redirectTo("/admin/balance").build()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<Rendering> handle(Exception error) {
        String message = error.getMessage();
        return Mono.just(
                Rendering.view("error")
                .status(BAD_REQUEST)
                .modelAttribute("message", message == null ? "Что то пошло не так" : message)
                .build()
        );
    }
}
