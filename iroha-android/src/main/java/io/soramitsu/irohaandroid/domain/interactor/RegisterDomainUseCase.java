package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.entity.reqest.DomainRegisterRequest;
import io.soramitsu.irohaandroid.domain.executor.PostExecutionThread;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import io.soramitsu.irohaandroid.domain.repository.DomainRepository;
import rx.Observable;

public class RegisterDomainUseCase extends UseCase {

    private DomainRegisterRequest body;
    private DomainRepository domainRepository;

    public RegisterDomainUseCase(DomainRegisterRequest body, DomainRepository domainRepository,
                                 ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.body = body;
        this.domainRepository = domainRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return domainRepository.register(body);
    }
}
