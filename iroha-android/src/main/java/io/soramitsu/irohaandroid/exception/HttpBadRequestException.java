package io.soramitsu.irohaandroid.exception;

public class HttpBadRequestException extends RuntimeException {
    public HttpBadRequestException() {
        super("");
    }

    public HttpBadRequestException(final String message) {
        super(message);
    }

    public HttpBadRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public HttpBadRequestException(final Throwable cause) {
        super(cause);
    }
}
