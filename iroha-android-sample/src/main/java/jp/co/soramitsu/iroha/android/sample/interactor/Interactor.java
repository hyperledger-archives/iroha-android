package jp.co.soramitsu.iroha.android.sample.interactor;


import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public abstract class Interactor<ResultType> {

    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final Scheduler jobScheduler;
    private final Scheduler uiScheduler;

    protected Interactor(Scheduler jobScheduler, Scheduler uiScheduler) {
        this.jobScheduler = jobScheduler;
        this.uiScheduler = uiScheduler;
    }

    protected abstract Single<ResultType> build();

    public void execute(Consumer<ResultType> onSuccess, Consumer<Throwable> onError) {
        subscriptions.add(build()
                .subscribeOn(jobScheduler)
                .observeOn(uiScheduler)
                .subscribe(onSuccess, onError));
    }

    public void unsubscribe() {
        subscriptions.clear();
    }
}