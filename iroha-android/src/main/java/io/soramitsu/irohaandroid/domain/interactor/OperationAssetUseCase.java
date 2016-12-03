package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.entity.reqest.AssetOperationRequest;
import io.soramitsu.irohaandroid.domain.executor.PostExecutionThread;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import io.soramitsu.irohaandroid.domain.repository.AssetRepository;
import rx.Observable;

public class OperationAssetUseCase extends UseCase {

    private AssetOperationRequest body;
    private AssetRepository assetRepository;

    public OperationAssetUseCase(AssetOperationRequest body, AssetRepository assetRepository,
                                 ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.body = body;
        this.assetRepository = assetRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return assetRepository.operation(body);
    }
}
