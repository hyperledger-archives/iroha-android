package io.soramitsu.irohaandroid.domain.executor;

public interface ThreadExecutor {
    void execute(final Runnable runnable);
}
