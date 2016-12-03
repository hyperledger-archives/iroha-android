package io.soramitsu.irohaandroid.data.entity.mapper;

import java.util.List;

import io.soramitsu.irohaandroid.data.entity.AssetEntity;
import io.soramitsu.irohaandroid.data.entity.AssetListEntity;
import io.soramitsu.irohaandroid.domain.entity.Asset;
import rx.Observable;
import rx.functions.Func1;

public class AssetEntityDataMapper {

    public Asset transform(AssetEntity assetEntity) {
        Asset asset = null;

        if (assetEntity != null) {
            asset = new Asset();
            asset.uuid = assetEntity.uuid;
            asset.name = assetEntity.name;
            asset.domain = assetEntity.domain;
            asset.creator = assetEntity.creator;
            asset.signature = assetEntity.signature;
            asset.value = assetEntity.value;
            asset.timestamp = assetEntity.timestamp;
        }

        return asset;
    }

    public List<Asset> transform(List<AssetEntity> assetListEntity) {
        Func1<AssetEntity, Asset> convertAction = new Func1<AssetEntity, Asset>() {
            @Override
            public Asset call(AssetEntity assetEntity) {
                return transform(assetEntity);
            }
        };
        return Observable.from(assetListEntity)
                .map(convertAction)
                .toList()
                .toBlocking()
                .single();
    }

    public List<Asset> transform(AssetListEntity assetListEntity) {
        return transform(assetListEntity.list);
    }
}
