package io.soramitsu.irohaandroid.entity.mapper;

import io.soramitsu.irohaandroid.entity.AccountEntity;
import io.soramitsu.irohaandroid.model.Account;

public class AccountEntityDataMapper {

    private final AssetEntityDataMapper assetEntityDataMapper;

    public AccountEntityDataMapper() {
        this.assetEntityDataMapper = new AssetEntityDataMapper();
    }

    public Account transform(AccountEntity accountEntity) {
        Account account = null;

        if (accountEntity != null) {
            account = new Account();
            account.uuid = accountEntity.uuid;
            account.alias = accountEntity.alias;
            if (accountEntity.assets != null) {
                account.assets = assetEntityDataMapper.transform(accountEntity.assets);
            }
        }

        return account;
    }
}
