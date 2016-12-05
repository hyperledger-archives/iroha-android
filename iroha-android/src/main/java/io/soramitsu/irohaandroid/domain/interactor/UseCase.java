package io.soramitsu.irohaandroid.domain.interactor;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public abstract class UseCase {

    private final Scheduler onObserveThread;
    private final Scheduler onSubscribeThread;

    private Subscription subscription = Subscriptions.empty();

    protected UseCase(Scheduler onSubscribeThread, Scheduler onObserveThread) {
        this.onSubscribeThread = onSubscribeThread;
        this.onObserveThread = onObserveThread;
    }

    protected abstract Observable buildUseCaseObservable();

    @SuppressWarnings("unchecked")
    public void execute(Subscriber useCaseSubscriber) {
        this.subscription = this.buildUseCaseObservable()
                .subscribeOn(onSubscribeThread)
                .observeOn(onObserveThread)
                .subscribe(useCaseSubscriber);
    }

    public void unsubscribe() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
