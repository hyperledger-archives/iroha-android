package jp.co.soramitsu.iroha.android.sample.interactor;


import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public abstract class Interactor {

    protected final CompositeDisposable subscriptions = new CompositeDisposable();
    protected final Scheduler jobScheduler;
    protected final Scheduler uiScheduler;

    protected Interactor(Scheduler jobScheduler, Scheduler uiScheduler) {
        this.jobScheduler = jobScheduler;
        this.uiScheduler = uiScheduler;
    }

    public void unsubscribe() {
        subscriptions.clear();
    }
}