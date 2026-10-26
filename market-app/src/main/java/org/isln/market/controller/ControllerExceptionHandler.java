package org.isln.market.controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.isln.market.exception.PaymentFailedException;
import org.isln.market.exception.PaymentServiceException;
import org.isln.payment.exception.InsufficientFundsException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(InsufficientFundsException.class)
    public Mono<String> handle(InsufficientFundsException error, Model model) {
        log.warn("Error handled in a controller: ", error);
        String message = error.getMessage();
        model.addAttribute("message", message == null ? "Недостаточно средств" : message);
        return Mono.just("error");
    }

    @ExceptionHandler(PaymentServiceException.class)
    public Mono<String> handle(PaymentServiceException error, Model model) {
        log.warn("Error handled in a controller: ", error);
        String message = error.getMessage();
        model.addAttribute("message", "Ошибка сервиса платежей");
        return Mono.just("error");
    }

    @ExceptionHandler(Exception.class)
    public Mono<String> handle(Exception error, Model model) {
        log.warn("Error handled in a controller: ", error);
        String message = error.getMessage();
        model.addAttribute("message", message == null ? "Что то пошло не так" : message);
        return Mono.just("error");
    }
}
