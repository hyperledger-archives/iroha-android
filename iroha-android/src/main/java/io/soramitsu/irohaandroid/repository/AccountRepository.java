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
