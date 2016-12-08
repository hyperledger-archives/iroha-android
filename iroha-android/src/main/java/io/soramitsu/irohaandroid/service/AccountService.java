package io.soramitsu.irohaandroid.service;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.mapper.AccountEntityDataMapper;
import io.soramitsu.irohaandroid.exception.AccountDuplicateException;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.exception.UserNotFoundException;
import io.soramitsu.irohaandroid.model.Account;
import io.soramitsu.irohaandroid.net.dataset.reqest.AccountRegisterRequest;
import io.soramitsu.irohaandroid.repository.AccountRepository;
import io.soramitsu.irohaandroid.repository.impl.AccountRepositoryImpl;

public class AccountService {

    private final AccountRepository accountRepository = new AccountRepositoryImpl();
    private final AccountEntityDataMapper accountEntityDataMapper = new AccountEntityDataMapper();

    public Account register(String publicKey, String alias)
            throws IOException, AccountDuplicateException, HttpBadRequestException {

        final AccountRegisterRequest body = new AccountRegisterRequest();
        body.publicKey = publicKey;
        body.alias = alias;
        body.timestamp = System.currentTimeMillis() / 1000;

        return accountEntityDataMapper.transform(accountRepository.register(body));
    }

    public Account findAccount(String uuid)
            throws IOException, UserNotFoundException, HttpBadRequestException {
        return accountEntityDataMapper.transform(accountRepository.findByUuid(uuid));
    }
}
