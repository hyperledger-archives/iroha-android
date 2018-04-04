package jp.co.soramitsu.iroha.android.sample.interactor;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import jp.co.soramitsu.iroha.android.sample.history.Transaction;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

public class GetTransactionsInteractor extends SingleInteractor<List<Transaction>, Void> {

    @Inject
    GetTransactionsInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                              @Named(ApplicationModule.UI) Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);
    }

    protected Single<List<Transaction>> build(Void v) {
        return Single.create(emitter -> {
            List<Transaction> transactions = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();

            transactions.add(new Transaction(1, calendar.getTime(), "Bulat", 1000));

            calendar.set(2018, 3, 1, 10, 30);
            transactions.add(new Transaction(2, calendar.getTime(), "Ali", -100));

            calendar.set(2018, 3, 1, 12, 34);
            transactions.add(new Transaction(3, calendar.getTime(), "Alex", 50));

            calendar.set(2018, 2, 30, 15, 1);
            transactions.add(new Transaction(4, calendar.getTime(), "Makoto", -200));

            calendar.set(2018, 2, 30, 18, 12);
            transactions.add(new Transaction(4, calendar.getTime(), "Ivan", 100));

            calendar.set(2018, 2, 30, 20, 32);
            transactions.add(new Transaction(4, calendar.getTime(), "Bob", -200));

            calendar.set(2018, 2, 29, 10, 5);
            transactions.add(new Transaction(4, calendar.getTime(), "Carl", -2000));

            emitter.onSuccess(transactions);
        });
    }
}
