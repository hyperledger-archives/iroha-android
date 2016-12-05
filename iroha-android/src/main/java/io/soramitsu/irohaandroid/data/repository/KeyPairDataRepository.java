package io.soramitsu.irohaandroid.data.repository;

import android.content.Context;

import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.data.cache.FileManager;
import io.soramitsu.irohaandroid.data.cache.KeyPairCache;
import io.soramitsu.irohaandroid.data.cache.KeyPairCacheImpl;
import io.soramitsu.irohaandroid.data.executor.JobExecutor;
import io.soramitsu.irohaandroid.data.repository.datasource.keypair.KeyPairDataFactory;
import io.soramitsu.irohaandroid.data.repository.datasource.keypair.KeyPairDataStore;
import io.soramitsu.irohaandroid.domain.entity.KeyPair;
import io.soramitsu.irohaandroid.domain.repository.KeyPairRepository;
import rx.Observable;

public class KeyPairDataRepository implements KeyPairRepository {
    private static KeyPairDataRepository keyPairDataRepository;

    private final KeyPairDataFactory keyPairDataFactory;

    private KeyPairDataRepository(Context context) {
        KeyPairCache keyPairCache = new KeyPairCacheImpl(
                context,
                new FileManager(),
                new JobExecutor()
        );
        this.keyPairDataFactory = new KeyPairDataFactory(keyPairCache);
    }

    public static KeyPairDataRepository getInstance(Context context) {
        if (keyPairDataRepository == null) {
            keyPairDataRepository = new KeyPairDataRepository(context);
        }

        return keyPairDataRepository;
    }

    @Override
    public Observable<Boolean> generate() {
        final KeyPair keyPair = Iroha.createKeyPair();
        return keyPairDataFactory.create().save(keyPair);
    }

    @Override
    public Observable<Boolean> remove() {
        return keyPairDataFactory.create().remove();
    }

    @Override
    public Observable<KeyPair> find() {
        KeyPairDataStore keyPairDataStore = keyPairDataFactory.create();
        return keyPairDataStore.find();
    }
}
