package io.soramitsu.irohaandroid.data.cache;

import io.soramitsu.irohaandroid.data.entity.AccountEntity;
import rx.Observable;

public interface AccountCache {
    String PREFERENCES_FILE_NAME = "io.soramitsu.irohaandroid.PREFERENCES";
    String PREFERENCES_KEY_UUID = "uuid";

    Observable<String> get();

    Observable<AccountEntity> get(final String uuid);

    void put(String uuid, AccountEntity accountEntity);

    void put(AccountEntity accountEntity);

    boolean isCached(final String uuid);

    boolean isExpired();

    void evictAll();
}
