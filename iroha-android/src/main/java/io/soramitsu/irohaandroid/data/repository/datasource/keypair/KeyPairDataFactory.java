package io.soramitsu.irohaandroid.data.repository.datasource.keypair;

import io.soramitsu.irohaandroid.data.cache.KeyPairCache;

public class KeyPairDataFactory {

    private final KeyPairCache keyPairCache;

    public KeyPairDataFactory(KeyPairCache keyPairCache) {
        this.keyPairCache = keyPairCache;
    }

    public KeyPairDataStore create() {
        return new DiskKeyPairDataStore(keyPairCache);
    }
}
