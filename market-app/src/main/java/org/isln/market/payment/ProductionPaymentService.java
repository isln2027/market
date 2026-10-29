package org.isln.market.payment;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.isln.market.client.PaymentApi;
import org.isln.market.exception.InsufficientFundsException;
import org.isln.market.exception.PaymentServiceException;
import org.isln.market.model.MoneyContainer;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("production")
@RequiredArgsConstructor
public class ProductionPaymentService implements PaymentService {
    private final PaymentApi paymentApi;

    @Override
    public Mono<Boolean> canPay(long amount) {
        return paymentApi.getBalance()
                .map(MoneyContainer::getAmount)
                .map(balance -> balance >= amount)
                .onErrorMap(Exception.class, PaymentServiceException::new);
    }

    @Override
    public Mono<Void> pay(long amount) {
        return paymentApi.payWithHttpInfo(new MoneyContainer().amount(amount))
                .map(ResponseEntity::getStatusCode)
                .flatMap(this::resolveCode)
                .onErrorMap(Exception.class, PaymentServiceException::new);
    }

    private Mono<Void> resolveCode(HttpStatusCode code) {
        return switch (code) {
            case HttpStatus.UNPROCESSABLE_CONTENT -> Mono.error(new InsufficientFundsException());
            case HttpStatus.OK -> Mono.empty();
            default -> Mono.error(new IllegalArgumentException("Неизвестный статус ответа: " + code));
        };
    }
}
