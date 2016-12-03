package io.soramitsu.irohaandroid.data.repository;

import android.content.Context;

import io.soramitsu.irohaandroid.data.cache.AccountCache;
import io.soramitsu.irohaandroid.data.cache.AccountCacheImpl;
import io.soramitsu.irohaandroid.data.cache.FileManager;
import io.soramitsu.irohaandroid.data.cache.serializer.JsonSerializer;
import io.soramitsu.irohaandroid.data.entity.AccountEntity;
import io.soramitsu.irohaandroid.data.entity.mapper.AccountEntityDataMapper;
import io.soramitsu.irohaandroid.data.executor.JobExecutor;
import io.soramitsu.irohaandroid.data.repository.datasource.account.AccountDataFactory;
import io.soramitsu.irohaandroid.data.repository.datasource.account.AccountDataStore;
import io.soramitsu.irohaandroid.domain.entity.Account;
import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import io.soramitsu.irohaandroid.domain.repository.AccountRepository;
import rx.Observable;
import rx.functions.Func1;

public class AccountDataRepository implements AccountRepository {

    private final Context context;
    private final AccountDataFactory accountDataFactory;
    private final AccountEntityDataMapper accountEntityDataMapper;

    private Func1<AccountEntity, Account> transformFunction = new Func1<AccountEntity, Account>() {
        @Override
        public Account call(AccountEntity accountEntity) {
            return accountEntityDataMapper.transform(accountEntity);
        }
    };

    public AccountDataRepository(Context context) {
        this.context = context;

        AccountCache accountCache = new AccountCacheImpl(
                this.context,
                new JsonSerializer(),
                new FileManager(),
                new JobExecutor()
        );
        accountDataFactory = new AccountDataFactory(accountCache);
        accountEntityDataMapper = new AccountEntityDataMapper();
    }

    @Override
    public Observable<Account> register(AccountRegisterRequest body) {
        AccountDataStore accountDataStore = accountDataFactory.create();
        return accountDataStore.register(body).map(transformFunction);
    }

    @Override
    public Observable<Account> userInfo(String uuid) {
        AccountDataStore accountDataStore = accountDataFactory.create(uuid);
        return accountDataStore.userInfo(uuid).map(transformFunction);
    }
}
