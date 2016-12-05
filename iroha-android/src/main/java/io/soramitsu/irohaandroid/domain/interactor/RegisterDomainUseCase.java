package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.entity.reqest.DomainRegisterRequest;
import io.soramitsu.irohaandroid.domain.repository.DomainRepository;
import rx.Observable;
import rx.Scheduler;

public class RegisterDomainUseCase extends UseCase {

    private DomainRegisterRequest body;
    private DomainRepository domainRepository;

    public RegisterDomainUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                                 DomainRegisterRequest body, DomainRepository domainRepository) {
        super(onSubscribeThread, onObserveThread);
        this.body = body;
        this.domainRepository = domainRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return domainRepository.register(body);
    }
}
