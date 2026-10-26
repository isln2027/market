package org.isln.payment.controller;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.payment.api.PaymentApi;
import org.isln.payment.api.PaymentResult;
import org.isln.payment.service.implementation.PaymentManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {
    private final PaymentManager paymentManager;
    @GetMapping("/can-pay")
    public Mono<Boolean> canPay(@RequestParam long amount) {
        return paymentManager.canPay(amount);
    }

    @PostMapping("/pay")
    public Mono<PaymentResult> pay(long amount) {
        return paymentManager.pay(amount);
    }
}
