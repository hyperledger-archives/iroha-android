package io.soramitsu.irohaandroid.domain.entity.response;


import java.util.Collection;

import io.soramitsu.irohaandroid.domain.entity.Asset;

public class AccountInfoResponse extends ResponseObject {
    public String alias;
    public Collection<Asset> assets;
}
