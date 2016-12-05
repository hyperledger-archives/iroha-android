package io.soramitsu.irohaandroid.data.repository.datasource.keypair;

import io.soramitsu.irohaandroid.data.cache.KeyPairCache;
import io.soramitsu.irohaandroid.data.exception.KeyPairNotFoundException;
import io.soramitsu.irohaandroid.domain.entity.KeyPair;
import rx.Observable;
import rx.Subscriber;

class DiskKeyPairDataStore implements KeyPairDataStore {

    private final KeyPairCache keyPairCache;

    DiskKeyPairDataStore(KeyPairCache keyPairCache) {
        this.keyPairCache = keyPairCache;
    }

    @Override
    public Observable<Boolean> save(final KeyPair keyPair) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (keyPair != null && !keyPair.isEmpty()) {
                    keyPairCache.put(keyPair);
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new KeyPairNotFoundException());
                }
            }
        });
    }

    @Override
    public Observable<Boolean> remove() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (keyPairCache.isCached()) {
                    keyPairCache.evictAll();
                }
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<KeyPair> find() {
        return keyPairCache.get();
    }
}
