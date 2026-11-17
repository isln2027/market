package org.isln.market.payment;

import java.security.Principal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.isln.market.client.PaymentApi;
import org.isln.market.exception.InsufficientFundsException;
import org.isln.market.exception.PaymentServiceException;
import org.isln.market.model.MoneyContainer;
import org.isln.market.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("production")
@RequiredArgsConstructor
public class ProductionPaymentService implements PaymentService {
    private final PaymentApi paymentApi;

    @Override
    public Mono<Boolean> canPay(long amount) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ProductionPaymentService::getAuthentication)
                .map(Principal::getName)
                .doOnNext(user -> log.info("Checking <{}> balance", user))
                .flatMap(name -> paymentApi.getBalance(user(name)))
                .doOnNext(mc -> log.info("Balance of <{}> received", mc.getUsername()))
                .doOnError(th -> log.info("Checking balance error", th))
                .map(MoneyContainer::getAmount)
                .map(balance -> balance >= amount)
                .onErrorMap(Exception.class, PaymentServiceException::new);
    }

    @Override
    public Mono<Void> pay(long amount) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ProductionPaymentService::getAuthentication)
                .map(Principal::getName)
                .doOnNext(user -> log.info("Paying for <{}> order", user))
                .flatMap(name -> paymentApi.payWithHttpInfo(new MoneyContainer().amount(amount).username(name)))
                .doOnError(th -> log.info("Payment error", th))
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

    private static User user(String name) {
        User user = new User();
        user.setUsername(name);
        return user;
    }

    private static Mono<Authentication> getAuthentication(SecurityContext ctx) {
        var auth = ctx.getAuthentication();
        if (auth == null) {
            return Mono.empty();
        } else {
            return Mono.just(auth);
        }
    }
}
