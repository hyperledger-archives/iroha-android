package io.soramitsu.iroha.exception;

public class IlligalRequestAmountException extends IllegalArgumentException {
    public IlligalRequestAmountException() {
    }

    public IlligalRequestAmountException(String s) {
        super(s);
    }

    public IlligalRequestAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public IlligalRequestAmountException(Throwable cause) {
        super(cause);
    }
}
