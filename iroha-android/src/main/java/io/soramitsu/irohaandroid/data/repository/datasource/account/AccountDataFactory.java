package io.soramitsu.irohaandroid.data.repository.datasource.account;

import io.soramitsu.irohaandroid.data.cache.AccountCache;
import io.soramitsu.irohaandroid.data.net.RestApi;
import okhttp3.OkHttpClient;

public class AccountDataFactory {

    private final AccountCache userCache;

    public enum FetchType {
        CLOUD, LOCAL
    }

    public AccountDataFactory(AccountCache userCache) {
        this.userCache = userCache;
    }

    public AccountDataStore create(FetchType fetchType) {
        if (fetchType == FetchType.LOCAL) {
            return new DiskAccountDataStore(userCache);
        } else {
            return createCloudDataStore();
        }
    }

    public AccountDataStore create(String uuid) {
        AccountDataStore userDataStore;

        if (!userCache.isExpired() && userCache.isCached(uuid)) {
            userDataStore = new DiskAccountDataStore(userCache);
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
