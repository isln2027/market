package org.isln.payment.controller;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.payment.api.PaymentApi;
import org.isln.payment.exception.InsufficientFundsException;
import org.isln.payment.model.MoneyContainer;
import org.isln.payment.service.PaymentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {
    private final PaymentService paymentService;

    @Override
    public Mono<ResponseEntity<MoneyContainer>> getBalance(ServerWebExchange exchange) {
        return paymentService.getBalance().map(balance ->
                ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new MoneyContainer().amount(balance))
        );
    }

    @Override
    public Mono<ResponseEntity<Void>> pay(Mono<MoneyContainer> moneyContainer, ServerWebExchange exchange) {
        return moneyContainer
                .flatMap(c -> paymentService.pay(c.getAmount()))
                .then(Mono.just(ResponseEntity.ok().body(null)));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Void> handleInsufficientFunds(InsufficientFundsException e) {
        return ResponseEntity.unprocessableContent().build();
    }
}
