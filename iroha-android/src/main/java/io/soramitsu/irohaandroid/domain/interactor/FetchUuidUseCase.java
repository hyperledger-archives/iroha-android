package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.repository.AccountRepository;
import rx.Observable;
import rx.Scheduler;

public class FetchUuidUseCase extends UseCase {

    private AccountRepository accountRepository;

    public FetchUuidUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                            AccountRepository accountRepository) {
        super(onSubscribeThread, onObserveThread);
        this.accountRepository = accountRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return accountRepository.findUuid();
    }

    public Observable<String> findUuid() {
        return buildUseCaseObservable();
    }
}
