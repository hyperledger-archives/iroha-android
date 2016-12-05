package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.repository.AssetRepository;
import rx.Observable;
import rx.Scheduler;

public class FetchAssetUseCase extends UseCase {

    private String domain;
    private AssetRepository assetRepository;

    public FetchAssetUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                             String domain, AssetRepository assetRepository) {
        super(onSubscribeThread, onObserveThread);
        this.domain = domain;
        this.assetRepository = assetRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return assetRepository.findAssets(domain);
    }
}
