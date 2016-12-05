package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.repository.TransactionRepository;
import rx.Observable;
import rx.Scheduler;

public class FetchMultiAssetsTransactionUseCase extends UseCase {

    private String uuid;
    private String domain;
    private String asset;
    private TransactionRepository transactionRepository;

    public FetchMultiAssetsTransactionUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                                              String uuid, String domain, String asset,
                                              TransactionRepository transactionRepository) {
        super(onSubscribeThread, onObserveThread);
        this.uuid = uuid;
        this.domain = domain;
        this.asset = asset;
        this.transactionRepository = transactionRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return transactionRepository.findHistory(uuid, domain, asset);
    }
}
