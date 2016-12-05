package io.soramitsu.irohaandroid.domain.interactor;

import java.util.List;

import io.soramitsu.irohaandroid.domain.entity.Account;
import io.soramitsu.irohaandroid.domain.entity.Transaction;
import io.soramitsu.irohaandroid.domain.entity.TransactionHistory;
import io.soramitsu.irohaandroid.domain.repository.AccountRepository;
import io.soramitsu.irohaandroid.domain.repository.TransactionRepository;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func2;

public class FetchTransactionHistoryUseCase extends UseCase {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    public FetchTransactionHistoryUseCase(Scheduler onSubscribeThread, Scheduler onObserveThread,
                                          AccountRepository accountRepository,
                                          TransactionRepository transactionRepository) {
        super(onSubscribeThread, onObserveThread);
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        final String uuid = accountRepository.findUuid().toBlocking().first();
        return Observable.zip(
                accountRepository.findByUuid(uuid),
                transactionRepository.findHistory(uuid),
                new Func2<Account, List<Transaction>, TransactionHistory>() {
                    @Override
                    public TransactionHistory call(Account account, List<Transaction> transactions) {
                        TransactionHistory transactionHistory = new TransactionHistory();
                        transactionHistory.value = account.assets.get(0).value; // TODO マルチアセット対応する
                        transactionHistory.histories = transactions;
                        return transactionHistory;
                    }
                }
        );
    }
}
