package io.soramitsu.iroha.exception;

public class LargeNumberOfDigitsException extends RuntimeException {
    public LargeNumberOfDigitsException() {
    }

    public LargeNumberOfDigitsException(String message) {
        super(message);
    }

    public LargeNumberOfDigitsException(String message, Throwable cause) {
        super(message, cause);
    }

    public LargeNumberOfDigitsException(Throwable cause) {
        super(cause);
    }
}
