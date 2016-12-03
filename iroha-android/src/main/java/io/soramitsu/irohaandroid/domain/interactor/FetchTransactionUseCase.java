package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.executor.PostExecutionThread;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import io.soramitsu.irohaandroid.domain.repository.TransactionRepository;
import rx.Observable;

public class FetchTransactionUseCase extends UseCase {

    private String uuid;
    private TransactionRepository transactionRepository;

    public FetchTransactionUseCase(String uuid, TransactionRepository transactionRepository,
                                   ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.uuid = uuid;
        this.transactionRepository = transactionRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return transactionRepository.history(uuid);
    }
}
