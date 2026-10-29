package org.isln.market.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Throwable cause) {
        super(cause);
    }

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException() {

    }
}
