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

package io.soramitsu.iroha.entity.mapper;

import io.soramitsu.iroha.entity.AccountEntity;
import io.soramitsu.iroha.model.Account;

public class AccountEntityDataMapper {
    public static Account transform(AccountEntity accountEntity) {
        Account account = null;

        if (accountEntity != null) {
            account = new Account();
            account.uuid = accountEntity.uuid;
            account.alias = accountEntity.alias;
            if (accountEntity.assets != null) {
                account.assets = AssetEntityDataMapper.transform(accountEntity.assets);
            }
        }

        return account;
    }
}
