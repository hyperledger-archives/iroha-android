package io.soramitsu.irohaandroid.data.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("アカウントが存在しません");
    }

    public UserNotFoundException(final String message) {
        super(message);
    }

    public UserNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(final Throwable cause) {
        super(cause);
    }
}
