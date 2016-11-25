package io.soramitsu.irohaandroid.domain.exception;

public interface ErrorBundle {
    Exception getException();

    String getErrorMessage();
}
