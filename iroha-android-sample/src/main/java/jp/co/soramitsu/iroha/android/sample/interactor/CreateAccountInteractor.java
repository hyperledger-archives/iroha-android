package jp.co.soramitsu.iroha.android.sample.interactor;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import jp.co.soramitsu.iroha.android.sample.IrohaConnection;
import jp.co.soramitsu.iroha.android.sample.history.Transaction;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

public class CreateAccountInteractor extends Interactor {

    IrohaConnection irohaConnection;

    @Inject
    CreateAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                            @Named(ApplicationModule.UI) Scheduler uiScheduler,
                            IrohaConnection irohaConnection) {
        super(jobScheduler, uiScheduler);
        this.irohaConnection = irohaConnection;
    }


    public void execute(Action onSuccess, Consumer<Throwable> onError, String username, String details) {
        subscriptions.add(build(username, details)
                .subscribeOn(jobScheduler)
                .observeOn(uiScheduler)
                .subscribe(onSuccess, onError));
    }

    protected Completable build(String username, String details) {
        return irohaConnection.execute(username, details);
    }
}
