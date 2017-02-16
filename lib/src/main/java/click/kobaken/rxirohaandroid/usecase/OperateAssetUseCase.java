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

package click.kobaken.rxirohaandroid.usecase;

import javax.inject.Inject;

import click.kobaken.rxirohaandroid.model.BaseModel;
import click.kobaken.rxirohaandroid.model.Transaction;
import click.kobaken.rxirohaandroid.net.dataset.reqest.AssetOperationRequest;
import click.kobaken.rxirohaandroid.repository.AssetRepository;
import io.reactivex.Observable;

public class OperateAssetUseCase {
    private final AssetRepository assetRepository;

    @Inject
    public OperateAssetUseCase(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public Observable<BaseModel> run(String assetUuid,
                                     String operateCommand,
                                     String assetValue,
                                     String assetSender,
                                     String assetReceiver,
                                     String assetSignature,
                                     long assetTimestamp) {
        return assetRepository.operation(new AssetOperationRequest() {{
            uuid = assetUuid;
            params = new Transaction.OperationParameter() {{
                command = operateCommand;
                value = assetValue;
                sender = assetSender;
                receiver = assetReceiver;
            }};
            signature = assetSignature;
            timestamp = assetTimestamp;
        }});
    }
}
