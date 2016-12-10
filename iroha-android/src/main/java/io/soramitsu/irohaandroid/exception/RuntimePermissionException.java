package io.soramitsu.irohaandroid.exception;

public class RuntimePermissionException extends RuntimeException {
    public RuntimePermissionException() {
    }

    public RuntimePermissionException(String message) {
        super(message);
    }

    public RuntimePermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimePermissionException(Throwable cause) {
        super(cause);
    }
}
