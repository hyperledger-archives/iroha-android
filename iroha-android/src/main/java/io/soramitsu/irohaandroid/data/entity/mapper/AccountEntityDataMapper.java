package io.soramitsu.irohaandroid.data.entity.mapper;

import io.soramitsu.irohaandroid.data.entity.AccountEntity;
import io.soramitsu.irohaandroid.domain.entity.Account;

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
            account.name = accountEntity.alias;
            account.assets = assetEntityDataMapper.transform(accountEntity.assets);
        }

        return account;
    }
}
