package io.soramitsu.irohaandroid.data.repository.datasource.asset;

import io.soramitsu.irohaandroid.data.entity.AssetEntity;
import io.soramitsu.irohaandroid.data.entity.AssetListEntity;
import io.soramitsu.irohaandroid.data.net.RestApi;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetRegisterRequest;
import rx.Observable;

public class CloudAssetDataStore implements AssetDataStore {

    private RestApi restApi;

    CloudAssetDataStore(RestApi restApi) {
        this.restApi = restApi;
    }

    @Override
    public Observable<AssetEntity> register(AssetRegisterRequest body) {
        return restApi.assetService().create(body);
    }

    @Override
    public Observable<AssetListEntity> assets(String domain) {
        return restApi.assetService().assets(domain);
    }

    @Override
    public Observable<AssetEntity> operation(AssetOperationRequest body) {
        return restApi.assetService().operation(body);
    }
}
