package org.isln.market.controller;

import java.nio.file.AccessDeniedException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.isln.market.exception.InsufficientFundsException;
import org.isln.market.exception.PaymentServiceException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public Mono<String> handle(AccessDeniedException error, Model model) {
        log.warn("Error handled in a controller: ", error);
        String message = error.getMessage();
        model.addAttribute("message", message == null ? "Нет доступа" : message);
        return Mono.just("error");
    }

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
