package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.entity.reqest.AssetRegisterRequest;
import io.soramitsu.irohaandroid.domain.executor.PostExecutionThread;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import io.soramitsu.irohaandroid.domain.repository.AssetRepository;
import rx.Observable;

public class RegisterAssetUseCase extends UseCase {

    private AssetRegisterRequest body;
    private AssetRepository assetRepository;

    public RegisterAssetUseCase(AssetRegisterRequest body, AssetRepository assetRepository,
                                ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.body = body;
        this.assetRepository = assetRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return assetRepository.create(body);
    }
}
