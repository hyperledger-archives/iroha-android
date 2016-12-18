package io.soramitsu.iroha.exception;

public class IllegalQRCodeException extends IllegalArgumentException {
    public IllegalQRCodeException() {
    }

    public IllegalQRCodeException(String s) {
        super(s);
    }

    public IllegalQRCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalQRCodeException(Throwable cause) {
        super(cause);
    }
}
