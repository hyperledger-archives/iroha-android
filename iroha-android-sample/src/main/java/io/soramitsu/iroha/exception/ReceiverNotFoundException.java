package io.soramitsu.iroha.exception;

public class ReceiverNotFoundException extends RuntimeException {
    public ReceiverNotFoundException() {
    }

    public ReceiverNotFoundException(String message) {
        super(message);
    }

    public ReceiverNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceiverNotFoundException(Throwable cause) {
        super(cause);
    }
}
