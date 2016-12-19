package io.soramitsu.irohaandroid.entity.mapper;

import java.util.ArrayList;
import java.util.List;

import io.soramitsu.irohaandroid.entity.AssetEntity;
import io.soramitsu.irohaandroid.entity.AssetListEntity;
import io.soramitsu.irohaandroid.model.Asset;

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
        List<Asset> assets = new ArrayList<>();
        for (AssetEntity assetEntity : assetListEntity) {
            assets.add(transform(assetEntity));
        }
        return assets;
    }

    public List<Asset> transform(AssetListEntity assetListEntity) {
        return transform(assetListEntity.list);
    }
}
