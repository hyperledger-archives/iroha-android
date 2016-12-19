package io.soramitsu.iroha.exception;

public class IllegalRequestAmountException extends IllegalArgumentException {
    public IllegalRequestAmountException() {
    }

    public IllegalRequestAmountException(String s) {
        super(s);
    }

    public IllegalRequestAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalRequestAmountException(Throwable cause) {
        super(cause);
    }
}
