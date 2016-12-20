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

package io.soramitsu.irohaandroid.service;

import java.io.IOException;
import java.util.List;

import io.soramitsu.irohaandroid.entity.mapper.AssetEntityDataMapper;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.model.Asset;
import io.soramitsu.irohaandroid.model.Transaction;
import io.soramitsu.irohaandroid.net.dataset.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.net.dataset.reqest.AssetRegisterRequest;
import io.soramitsu.irohaandroid.repository.AssetRepository;
import io.soramitsu.irohaandroid.repository.impl.AssetRepositoryImpl;

public class AssetService {

    private final AssetRepository assetRepository = new AssetRepositoryImpl();
    private final AssetEntityDataMapper assetEntityDataMapper = new AssetEntityDataMapper();

    public Asset create(String name, String domain, String creator, String signature, long timestamp)
            throws IOException, HttpBadRequestException {

        final AssetRegisterRequest body = new AssetRegisterRequest();
        body.name = name;
        body.domain = domain;
        body.creator = creator;
        body.timestamp = timestamp;
        body.signature = signature;

        return assetEntityDataMapper.transform(assetRepository.create(body));
    }

    public List<Asset> findAssets(String domain, int limit, int offset)
            throws IOException, HttpBadRequestException {
        return assetEntityDataMapper.transform(assetRepository.findAssets(domain, limit, offset));
    }

    public boolean operation(String assetUuid, String command, String value,
                             String sender, String receiver, String signature, long timestamp)
            throws IOException, HttpBadRequestException {

        final AssetOperationRequest body = new AssetOperationRequest();
        body.uuid = assetUuid;
        body.params = new Transaction.OperationParameter();
        body.params.command = command;
        body.params.value = value;
        body.params.sender = sender;
        body.params.receiver = receiver;
        body.timestamp = timestamp;
        body.signature = signature;

        return assetRepository.operation(body);
    }
}
