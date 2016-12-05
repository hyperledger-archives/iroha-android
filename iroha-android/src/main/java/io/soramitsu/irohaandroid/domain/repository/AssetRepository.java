package io.soramitsu.irohaandroid.domain.repository;

import java.util.List;

import io.soramitsu.irohaandroid.domain.entity.Asset;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetRegisterRequest;
import rx.Observable;

public interface AssetRepository {
    Observable<Asset> create(AssetRegisterRequest body);

    Observable<List<Asset>> findAssets(String domain);

    Observable<Asset> operation(AssetOperationRequest body);
}
