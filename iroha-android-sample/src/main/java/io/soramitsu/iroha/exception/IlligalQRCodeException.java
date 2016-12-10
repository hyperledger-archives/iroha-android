package io.soramitsu.iroha.exception;

public class IlligalQRCodeException extends IllegalArgumentException {
    public IlligalQRCodeException() {
    }

    public IlligalQRCodeException(String s) {
        super(s);
    }

    public IlligalQRCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IlligalQRCodeException(Throwable cause) {
        super(cause);
    }
}
