package io.soramitsu.iroha.exception;

public class SelfSendCanNotException extends RuntimeException {
    public SelfSendCanNotException() {
    }

    public SelfSendCanNotException(String message) {
        super(message);
    }

    public SelfSendCanNotException(String message, Throwable cause) {
        super(message, cause);
    }

    public SelfSendCanNotException(Throwable cause) {
        super(cause);
    }
}
