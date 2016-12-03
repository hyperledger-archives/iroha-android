package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.executor.PostExecutionThread;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import io.soramitsu.irohaandroid.domain.repository.DomainRepository;
import rx.Observable;

public class FetchDomainUseCase extends UseCase {

    private DomainRepository domainRepository;

    public FetchDomainUseCase(DomainRepository domainRepository,
                              ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.domainRepository = domainRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return domainRepository.domains();
    }
}
