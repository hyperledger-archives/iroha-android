package io.soramitsu.irohaandroid.domain.repository;

import io.soramitsu.irohaandroid.domain.entity.Account;
import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import rx.Observable;

public interface AccountRepository {
    Observable<Account> register(AccountRegisterRequest body);

    Observable<Account> userInfo(String uuid);
}
