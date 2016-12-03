package io.soramitsu.irohaandroid.data.repository;

import java.util.List;

import io.soramitsu.irohaandroid.data.entity.AssetEntity;
import io.soramitsu.irohaandroid.data.entity.AssetListEntity;
import io.soramitsu.irohaandroid.data.entity.mapper.AssetEntityDataMapper;
import io.soramitsu.irohaandroid.data.repository.datasource.asset.AssetDataFactory;
import io.soramitsu.irohaandroid.data.repository.datasource.asset.AssetDataStore;
import io.soramitsu.irohaandroid.domain.entity.Asset;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.domain.entity.reqest.AssetRegisterRequest;
import io.soramitsu.irohaandroid.domain.repository.AssetRepository;
import rx.Observable;
import rx.functions.Func1;

public class AssetDataRepository implements AssetRepository {

    private AssetDataFactory assetDataFactory;
    private AssetEntityDataMapper assetEntityDataMapper;

    public AssetDataRepository() {
        this.assetDataFactory = new AssetDataFactory();
        this.assetEntityDataMapper = new AssetEntityDataMapper();
    }

    @Override
    public Observable<Asset> create(AssetRegisterRequest body) {
        AssetDataStore assetDataStore = assetDataFactory.create();
        return assetDataStore.create(body).map(new Func1<AssetEntity, Asset>() {
            @Override
            public Asset call(AssetEntity assetEntity) {
                return assetEntityDataMapper.transform(assetEntity);
            }
        });
    }

    @Override
    public Observable<List<Asset>> assets(String domain) {
        AssetDataStore assetDataStore = assetDataFactory.create();
        return assetDataStore.assets(domain).map(new Func1<AssetListEntity, List<Asset>>() {
            @Override
            public List<Asset> call(AssetListEntity assetListEntity) {
                return assetEntityDataMapper.transform(assetListEntity);
            }
        });
    }

    @Override
    public Observable<Asset> operation(AssetOperationRequest body) {
        final AssetDataStore assetDataStore = assetDataFactory.create();
        return assetDataStore.operation(body).map(new Func1<AssetEntity, Asset>() {
            @Override
            public Asset call(AssetEntity assetEntity) {
                return assetEntityDataMapper.transform(assetEntity);
            }
        });
    }
}
