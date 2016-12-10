package io.soramitsu.iroha.exception;

public class RequiredArgumentException extends RuntimeException {
    public RequiredArgumentException() {
    }

    public RequiredArgumentException(String message) {
        super(message);
    }

    public RequiredArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequiredArgumentException(Throwable cause) {
        super(cause);
    }
}
