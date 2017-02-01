/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

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

package click.kobaken.rxirohaandroid.service;

import java.util.List;

import click.kobaken.rxirohaandroid.model.Asset;
import click.kobaken.rxirohaandroid.model.BaseModel;
import click.kobaken.rxirohaandroid.model.Transaction;
import click.kobaken.rxirohaandroid.net.dataset.reqest.AssetOperationRequest;
import click.kobaken.rxirohaandroid.net.dataset.reqest.AssetRegisterRequest;
import click.kobaken.rxirohaandroid.repository.AssetRepository;
import io.reactivex.Observable;

public class AssetService {
    private AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public Observable<Asset> create(
            String name, String domain, String creator, String signature, long timestamp) {

        final AssetRegisterRequest body = new AssetRegisterRequest();
        body.name = name;
        body.domain = domain;
        body.creator = creator;
        body.timestamp = timestamp;
        body.signature = signature;

        return assetRepository.create(body);
    }

    public Observable<List<Asset>> findAssets(String domain, int limit, int offset) {
        return assetRepository.findAssets(domain, limit, offset);
    }

    public Observable<BaseModel> operation(String assetUuid, String command, String value,
                                           String sender, String receiver, String signature, long timestamp) {

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
