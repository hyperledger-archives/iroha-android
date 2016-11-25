package io.soramitsu.irohaandroid.domain.entity.response;

import java.util.Collection;

import io.soramitsu.irohaandroid.domain.entity.Asset;

public class AssetListResponse extends ResponseObject {
    public Collection<Asset> list;
}
