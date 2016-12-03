package io.soramitsu.irohaandroid.data.repository.datasource.account;

import io.soramitsu.irohaandroid.data.cache.AccountCache;
import io.soramitsu.irohaandroid.data.net.RestApi;
import okhttp3.OkHttpClient;

public class AccountDataFactory {

    private final AccountCache userCache;

    public AccountDataFactory(AccountCache userCache) {
        this.userCache = userCache;
    }

    public AccountDataStore create() {
        return createCloudDataStore();
    }

    public AccountDataStore create(String uuid) {
        AccountDataStore userDataStore;

        if (!this.userCache.isExpired() && this.userCache.isCached(uuid)) {
            userDataStore = new DiskAccountDataStore(this.userCache);
        } else {
            userDataStore = createCloudDataStore();
        }

        return userDataStore;
    }

    private AccountDataStore createCloudDataStore() {
        RestApi restApi = new RestApi(new OkHttpClient.Builder().build());
        return new CloudAccountDataStore(restApi, userCache);
    }
}
