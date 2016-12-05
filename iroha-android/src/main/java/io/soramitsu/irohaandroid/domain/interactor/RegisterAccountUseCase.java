package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.entity.reqest.AccountRegisterRequest;
import io.soramitsu.irohaandroid.domain.repository.AccountRepository;
import rx.Observable;
import rx.Scheduler;

public class RegisterAccountUseCase extends UseCase {

    private AccountRegisterRequest body;
    private AccountRepository accountRepository;

    public RegisterAccountUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                                  AccountRegisterRequest body, AccountRepository accountRepository) {
        super(onSubscribeThread, onObserveThread);
        this.body = body;
        this.accountRepository = accountRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return accountRepository.register(body);
    }
}
