package io.soramitsu.irohaandroid.exception;

public class AccountDuplicateException extends RuntimeException {
    public AccountDuplicateException() {
        super("アカウントが重複しています");
    }

    public AccountDuplicateException(final String message) {
        super(message);
    }

    public AccountDuplicateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AccountDuplicateException(final Throwable cause) {
        super(cause);
    }
}
