package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.entity.KeyPair;
import io.soramitsu.irohaandroid.domain.repository.KeyPairRepository;
import rx.Observable;
import rx.Scheduler;

public class FetchKeyPairUseCase extends UseCase {

    private KeyPairRepository keyPairRepository;

    public FetchKeyPairUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                               KeyPairRepository keyPairRepository) {
        super(onSubscribeThread, onObserveThread);
        this.keyPairRepository = keyPairRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return keyPairRepository.find();
    }

    public Observable<KeyPair> findKeyPair() {
        return buildUseCaseObservable();
    }
}
