package io.soramitsu.irohaandroid.data.repository.datasource.transaction;

import io.soramitsu.irohaandroid.data.cache.TransactionListCache;
import io.soramitsu.irohaandroid.data.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.data.net.RestApi;
import rx.Observable;
import rx.functions.Action1;

class CloudTransactionDataStore implements TransactionDataStore {

    private final RestApi restApi;
    private final TransactionListCache transactionListCache;

    CloudTransactionDataStore(RestApi restApi, TransactionListCache transactionListCache) {
        this.restApi = restApi;
        this.transactionListCache = transactionListCache;
    }

    @Override
    public Observable<TransactionListEntity> history(final String uuid) {
        return restApi.transactionService().history(uuid).doOnNext(new Action1<TransactionListEntity>() {
            @Override
            public void call(TransactionListEntity transactionListEntity) {
                if (transactionListEntity != null) {
                    transactionListCache.put(uuid, transactionListEntity);
                }
            }
        });
    }

    @Override
    public Observable<TransactionListEntity> history(final String uuid, final String domain, final String asset) {
        return restApi.transactionService().history(uuid, domain, asset).doOnNext(new Action1<TransactionListEntity>() {
            @Override
            public void call(TransactionListEntity transactionListEntity) {
                if (transactionListEntity != null) {
                    transactionListCache.put(uuid, domain, asset, transactionListEntity);
                }
            }
        });
    }
}
