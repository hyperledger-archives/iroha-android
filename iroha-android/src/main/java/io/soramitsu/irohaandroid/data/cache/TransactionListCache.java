package io.soramitsu.irohaandroid.data.cache;

import io.soramitsu.irohaandroid.data.entity.TransactionListEntity;
import rx.Observable;

public interface TransactionListCache {
    Observable<TransactionListEntity> get(final String uuid);

    Observable<TransactionListEntity> get(final String uuid, final String domain, final String asset);

    void put(final String uuid, final TransactionListEntity transactionListEntity);

    void put(final String uuid, final String domain, final String asset, final TransactionListEntity transactionListEntity);

    boolean isCached(final String uuid, String... other);

    boolean isExpired();

    void evictAll();
}
