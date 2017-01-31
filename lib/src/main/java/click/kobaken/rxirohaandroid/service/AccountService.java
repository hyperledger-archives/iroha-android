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

package click.kobaken.rxirohaandroid.service;

import click.kobaken.rxirohaandroid.model.Account;
import click.kobaken.rxirohaandroid.net.dataset.reqest.AccountRegisterRequest;
import click.kobaken.rxirohaandroid.repository.AccountRepository;
import io.reactivex.Observable;

public class AccountService {
    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Observable<Account> register(String publicKey, String alias) {

        final AccountRegisterRequest body = new AccountRegisterRequest();
        body.publicKey = publicKey;
        body.alias = alias;
        body.timestamp = System.currentTimeMillis() / 1000;
        return accountRepository.register(body);
    }

    public Observable<Account> findAccount(String uuid) {
        return accountRepository.findByUuid(uuid);
    }
}
