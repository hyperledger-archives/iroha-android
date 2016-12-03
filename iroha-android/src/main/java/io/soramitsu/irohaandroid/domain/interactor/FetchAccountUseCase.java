package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.executor.PostExecutionThread;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import io.soramitsu.irohaandroid.domain.repository.AccountRepository;
import rx.Observable;

public class FetchAccountUseCase extends UseCase {

    private String uuid;
    private AccountRepository accountRepository;

    public FetchAccountUseCase(String uuid, AccountRepository accountRepository,
                               ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.uuid = uuid;
        this.accountRepository = accountRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return accountRepository.userInfo(uuid);
    }
}
