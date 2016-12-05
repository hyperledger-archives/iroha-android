package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.entity.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.domain.repository.AssetRepository;
import rx.Observable;
import rx.Scheduler;

public class OperationAssetUseCase extends UseCase {

    private AssetOperationRequest body;
    private AssetRepository assetRepository;

    public OperationAssetUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                                 AssetOperationRequest body, AssetRepository assetRepository) {
        super(onSubscribeThread, onObserveThread);
        this.body = body;
        this.assetRepository = assetRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return assetRepository.operation(body);
    }
}
