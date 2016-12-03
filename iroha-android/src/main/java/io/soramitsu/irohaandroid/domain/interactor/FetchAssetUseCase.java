package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.executor.PostExecutionThread;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import io.soramitsu.irohaandroid.domain.repository.AssetRepository;
import rx.Observable;

public class FetchAssetUseCase extends UseCase {

    private String domain;
    private AssetRepository assetRepository;

    public FetchAssetUseCase(String domain, AssetRepository assetRepository,
                             ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.domain = domain;
        this.assetRepository = assetRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return assetRepository.assets(domain);
    }
}
