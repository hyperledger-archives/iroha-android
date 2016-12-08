package io.soramitsu.irohaandroid.callback;

public interface Callback<T> {
    void onSuccessful(T result);
    void onFailure(Throwable throwable);
}
