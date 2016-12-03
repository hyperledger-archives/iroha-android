package io.soramitsu.irohaandroid.data.repository.datasource.asset;

import io.soramitsu.irohaandroid.data.entity.AssetEntity;
import io.soramitsu.irohaandroid.data.entity.AssetListEntity;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetRegisterRequest;
import rx.Observable;

public interface AssetDataStore {
    Observable<AssetEntity> create(AssetRegisterRequest body);
    Observable<AssetListEntity> assets(String domain);
    Observable<AssetEntity> operation(AssetOperationRequest body);
}
