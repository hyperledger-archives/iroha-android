package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.executor.PostExecutionThread;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import io.soramitsu.irohaandroid.domain.repository.TransactionRepository;
import rx.Observable;

public class FetchMultiAssetsTransactionUseCase extends UseCase {

    private String uuid;
    private String domain;
    private String asset;
    private TransactionRepository transactionRepository;

    public FetchMultiAssetsTransactionUseCase(String uuid, String domain, String asset, TransactionRepository transactionRepository,
                                              ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.uuid = uuid;
        this.domain = domain;
        this.asset = asset;
        this.transactionRepository = transactionRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return transactionRepository.history(uuid, domain, asset);
    }
}
