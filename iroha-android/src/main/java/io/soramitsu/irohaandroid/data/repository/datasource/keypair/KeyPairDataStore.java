package io.soramitsu.irohaandroid.data.repository.datasource.keypair;

import io.soramitsu.irohaandroid.domain.entity.KeyPair;
import rx.Observable;

public interface KeyPairDataStore {
    Observable<Boolean> save(KeyPair keyPair);
    Observable<Boolean> remove();
    Observable<KeyPair> find();
}
