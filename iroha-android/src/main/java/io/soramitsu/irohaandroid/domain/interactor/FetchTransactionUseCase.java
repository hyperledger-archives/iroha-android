package io.soramitsu.irohaandroid.domain.interactor;

import io.soramitsu.irohaandroid.domain.repository.TransactionRepository;
import rx.Observable;
import rx.Scheduler;

public class FetchTransactionUseCase extends UseCase {

    private String uuid;
    private TransactionRepository transactionRepository;

    public FetchTransactionUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                                   String uuid, TransactionRepository transactionRepository) {
        super(onSubscribeThread, onObserveThread);
        this.uuid = uuid;
        this.transactionRepository = transactionRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return transactionRepository.findHistory(uuid);
    }
}
