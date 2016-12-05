package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.entity.reqest.AssetRegisterRequest;
import io.soramitsu.irohaandroid.domain.repository.AssetRepository;
import rx.Observable;
import rx.Scheduler;

public class RegisterAssetUseCase extends UseCase {

    private AssetRegisterRequest body;
    private AssetRepository assetRepository;

    public RegisterAssetUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                                AssetRegisterRequest body, AssetRepository assetRepository) {
        super(onSubscribeThread, onObserveThread);
        this.body = body;
        this.assetRepository = assetRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return assetRepository.create(body);
    }
}
