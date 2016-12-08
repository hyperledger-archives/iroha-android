package io.soramitsu.irohaandroid.entity;

import java.util.List;

public class AccountEntity extends BaseEntity {
    public String uuid;
    public String alias;
    public List<AssetEntity> assets;
}
