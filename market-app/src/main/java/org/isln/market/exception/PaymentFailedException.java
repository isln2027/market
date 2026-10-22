package org.isln.market.exception;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }

    public PaymentFailedException() {

    }
}
