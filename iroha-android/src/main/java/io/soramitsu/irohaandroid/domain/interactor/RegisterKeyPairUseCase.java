package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.repository.KeyPairRepository;
import rx.Observable;
import rx.Scheduler;

public class RegisterKeyPairUseCase extends UseCase {

    private KeyPairRepository keyPairRepository;

    public RegisterKeyPairUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                                  KeyPairRepository keyPairRepository) {
        super(onSubscribeThread, onObserveThread);
        this.keyPairRepository = keyPairRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return keyPairRepository.generate();
    }
}
