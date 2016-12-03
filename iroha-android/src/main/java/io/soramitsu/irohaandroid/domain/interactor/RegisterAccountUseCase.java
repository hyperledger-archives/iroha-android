package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import io.soramitsu.irohaandroid.domain.executor.PostExecutionThread;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import io.soramitsu.irohaandroid.domain.repository.AccountRepository;
import rx.Observable;

public class RegisterAccountUseCase extends UseCase {

    private AccountRegisterRequest body;
    private AccountRepository accountRepository;

    public RegisterAccountUseCase(AccountRegisterRequest body, AccountRepository accountRepository,
                                  ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.body = body;
        this.accountRepository = accountRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return accountRepository.register(body);
    }
}
