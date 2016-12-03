package io.soramitsu.irohaandroid.data.repository.datasource.account;

import io.soramitsu.irohaandroid.data.cache.AccountCache;
import io.soramitsu.irohaandroid.data.entity.AccountEntity;
import io.soramitsu.irohaandroid.data.exception.UnableAccessException;
import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import rx.Observable;

class DiskAccountDataStore implements AccountDataStore {

    private final AccountCache userCache;

    DiskAccountDataStore(AccountCache userCache) {
        this.userCache = userCache;
    }

    @Override
    public Observable<AccountEntity> register(AccountRegisterRequest body) {
        throw new UnableAccessException(DiskAccountDataStore.class.getName());
    }

    @Override
    public Observable<AccountEntity> userInfo(String uuid) {
        return userCache.get(uuid);
    }
}
