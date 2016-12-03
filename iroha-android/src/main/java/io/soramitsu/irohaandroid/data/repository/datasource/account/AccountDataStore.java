package io.soramitsu.irohaandroid.data.repository.datasource.account;

import io.soramitsu.irohaandroid.data.entity.AccountEntity;
import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import rx.Observable;

public interface AccountDataStore {
    Observable<AccountEntity> register(AccountRegisterRequest body);
    Observable<AccountEntity> userInfo(String uuid);
}
