package io.soramitsu.iroha.exception;

public class NetworkNotConnectedException extends RuntimeException {
    public NetworkNotConnectedException() {
    }

    public NetworkNotConnectedException(String message) {
        super(message);
    }

    public NetworkNotConnectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkNotConnectedException(Throwable cause) {
        super(cause);
    }
}
