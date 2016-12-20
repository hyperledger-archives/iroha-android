/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
