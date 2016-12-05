package io.soramitsu.irohaandroid.data.cache;

import io.soramitsu.irohaandroid.domain.entity.KeyPair;
import rx.Observable;

public interface KeyPairCache {
    String PREFERENCES_KEY_PUBLIC_KEY = "public_key";
    String PREFERENCES_KEY_PRIVATE_KEY = "private_key";

    Observable<KeyPair> get();

    void put(KeyPair keyPairEntity);

    boolean isCached();

    void evictAll();
}
