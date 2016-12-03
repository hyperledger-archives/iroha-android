package io.soramitsu.irohaandroid.data.repository.datasource.transaction;

import io.soramitsu.irohaandroid.data.cache.TransactionListCache;
import io.soramitsu.irohaandroid.data.entity.TransactionListEntity;
import rx.Observable;

class DiskTransactionDataStore implements TransactionDataStore {

    private final TransactionListCache transactionListCache;

    DiskTransactionDataStore(TransactionListCache transactionListCache) {
        this.transactionListCache = transactionListCache;
    }

    @Override
    public Observable<TransactionListEntity> history(String uuid) {
        return transactionListCache.get(uuid);
    }

    @Override
    public Observable<TransactionListEntity> history(String uuid, String domain, String asset) {
        return transactionListCache.get(uuid, domain, asset);
    }
}
