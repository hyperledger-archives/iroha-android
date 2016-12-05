package io.soramitsu.irohaandroid.domain.repository;

import io.soramitsu.irohaandroid.domain.entity.KeyPair;
import rx.Observable;

public interface KeyPairRepository {
    Observable<Boolean> generate();
    Observable<Boolean> remove();
    Observable<KeyPair> find();
}
