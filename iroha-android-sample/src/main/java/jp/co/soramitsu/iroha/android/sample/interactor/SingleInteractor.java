package jp.co.soramitsu.iroha.android.sample.interactor;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public abstract class SingleInteractor<ResultType, ParameterType> extends Interactor {

    SingleInteractor(Scheduler jobScheduler, Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);
    }

    protected abstract Single<ResultType> build(ParameterType parameter);

    public void execute(ParameterType parameter, Consumer<ResultType> onSuccess, Consumer<Throwable> onError) {
        subscriptions.add(build(parameter)
                .subscribeOn(jobScheduler)
                .observeOn(uiScheduler)
                .subscribe(onSuccess, onError));
    }

    public void execute(Consumer<ResultType> onSuccess, Consumer<Throwable> onError) {
        execute(null, onSuccess, onError);
    }
}