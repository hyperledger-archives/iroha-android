package io.soramitsu.irohaandroid.data.repository.datasource.transaction;

import io.soramitsu.irohaandroid.data.cache.TransactionListCache;
import io.soramitsu.irohaandroid.data.net.RestApi;
import okhttp3.OkHttpClient;

public class TransactionDataFactory {

    private final TransactionListCache transactionListCache;

    public TransactionDataFactory(TransactionListCache transactionListCache) {
        this.transactionListCache = transactionListCache;
    }

    public TransactionDataStore create(String uuid) {
        TransactionDataStore userDataStore;

        if (!transactionListCache.isExpired() && transactionListCache.isCached(uuid)) {
            userDataStore = new DiskTransactionDataStore(transactionListCache);
        } else {
            userDataStore = createCloudDataStore();
        }

        return userDataStore;
    }

    public TransactionDataStore create(String uuid, String domain, String asset) {
        TransactionDataStore userDataStore;

        if (!transactionListCache.isExpired() && transactionListCache.isCached(uuid, domain, asset)) {
            userDataStore = new DiskTransactionDataStore(transactionListCache);
        } else {
            userDataStore = createCloudDataStore();
        }

        return userDataStore;
    }

    private TransactionDataStore createCloudDataStore() {
        RestApi restApi = new RestApi(new OkHttpClient.Builder().build());
        return new CloudTransactionDataStore(restApi, transactionListCache);
    }
}
