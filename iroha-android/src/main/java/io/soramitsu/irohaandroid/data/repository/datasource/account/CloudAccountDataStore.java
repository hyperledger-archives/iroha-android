package io.soramitsu.irohaandroid.data.repository.datasource.account;

import io.soramitsu.irohaandroid.data.cache.AccountCache;
import io.soramitsu.irohaandroid.data.entity.AccountEntity;
import io.soramitsu.irohaandroid.data.exception.UnableAccessException;
import io.soramitsu.irohaandroid.data.net.RestApi;
import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import rx.Observable;
import rx.functions.Action1;

class CloudAccountDataStore implements AccountDataStore {

    private final RestApi restApi;
    private final AccountCache accountCache;

    CloudAccountDataStore(RestApi restApi, AccountCache accountCache) {
        this.restApi = restApi;
        this.accountCache = accountCache;
    }

    @Override
    public Observable<AccountEntity> register(AccountRegisterRequest body) {
        return restApi.accountService().register(body).doOnNext(new Action1<AccountEntity>() {
            @Override
            public void call(AccountEntity accountEntity) {
                if (accountEntity != null) {
                    accountCache.put(accountEntity.uuid, accountEntity);
                }
            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                accountCache.put("test", new AccountEntity()); // TODO mock用
            }
        });
    }

    @Override
    public Observable<String> uuid() {
        throw new UnableAccessException(CloudAccountDataStore.class.getName());
    }

    @Override
    public Observable<AccountEntity> userInfo(String uuid) {
        return restApi.accountService().userInfo(uuid).doOnNext(new Action1<AccountEntity>() {
            @Override
            public void call(AccountEntity accountEntity) {
                if (accountEntity != null) {
                    accountCache.put(accountEntity);
                }
            }
        });
    }
}
