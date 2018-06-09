package jp.co.soramitsu.iroha.android.sample.interactor;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public abstract class CompletableInteractor<ParameterType> extends Interactor {

    CompletableInteractor(Scheduler jobScheduler, Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);
    }

    protected abstract Completable build(ParameterType parameter);

    public void execute(ParameterType parameter, Action onSuccess, Consumer<Throwable> onError) {
        subscriptions.add(build(parameter)
                .subscribeOn(jobScheduler)
                .observeOn(uiScheduler)
                .subscribe(onSuccess, onError));
    }
}