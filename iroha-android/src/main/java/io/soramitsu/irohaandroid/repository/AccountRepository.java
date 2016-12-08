package io.soramitsu.irohaandroid.repository;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.AccountEntity;
import io.soramitsu.irohaandroid.exception.AccountDuplicateException;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.exception.UserNotFoundException;
import io.soramitsu.irohaandroid.net.dataset.reqest.AccountRegisterRequest;

public interface AccountRepository {
    AccountEntity register(AccountRegisterRequest body)
            throws IOException, AccountDuplicateException, HttpBadRequestException;

    AccountEntity findByUuid(String uuid)
            throws IOException, UserNotFoundException, HttpBadRequestException;
}
