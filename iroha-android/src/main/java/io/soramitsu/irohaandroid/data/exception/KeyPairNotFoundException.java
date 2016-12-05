package io.soramitsu.irohaandroid.data.exception;

public class KeyPairNotFoundException extends Exception {
    public KeyPairNotFoundException() {
        super("KeyPairが存在しません");
    }

    public KeyPairNotFoundException(final String message) {
        super(message);
    }

    public KeyPairNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public KeyPairNotFoundException(final Throwable cause) {
        super(cause);
    }
}
