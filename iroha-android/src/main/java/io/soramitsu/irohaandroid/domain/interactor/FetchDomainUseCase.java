package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.repository.DomainRepository;
import rx.Observable;
import rx.Scheduler;

public class FetchDomainUseCase extends UseCase {

    private DomainRepository domainRepository;

    public FetchDomainUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                              DomainRepository domainRepository) {
        super(onSubscribeThread, onObserveThread);
        this.domainRepository = domainRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return domainRepository.findDomains();
    }
}
