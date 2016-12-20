/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
