package io.soramitsu.irohaandroid.exception;

public class UnableAccessException extends RuntimeException {

    public UnableAccessException() {
        super();
    }

    public UnableAccessException(final String calledAt) {
        super(Thread.currentThread().getStackTrace()[3].getMethodName() + " can not access at " + calledAt);
    }

    public UnableAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnableAccessException(final Throwable cause) {
        super(cause);
    }
}
