package io.soramitsu.irohaandroid.data.cache;

import android.content.Context;

import java.io.File;

import io.soramitsu.irohaandroid.data.exception.UserNotFoundException;
import io.soramitsu.irohaandroid.domain.entity.KeyPair;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;

import static io.soramitsu.irohaandroid.data.cache.FileManager.PREFERENCES_FILE_NAME;

public class KeyPairCacheImpl implements KeyPairCache {
    private static final String DEFAULT_FILE_NAME = "keypair_";

    private final Context context;
    private final File cacheDir;
    private final FileManager fileManager;
    private final ThreadExecutor threadExecutor;

    public KeyPairCacheImpl(Context context, FileManager fileManager, ThreadExecutor threadExecutor) {
        if (context == null || fileManager == null || threadExecutor == null) {
            throw new IllegalArgumentException("Invalid null parameter");
        }
        this.context = context;
        this.cacheDir = context.getCacheDir();
        this.fileManager = fileManager;
        this.threadExecutor = threadExecutor;
    }

    @Override
    public Observable<KeyPair> get() {
        return Observable.create(new Observable.OnSubscribe<KeyPair>() {
            @Override
            public void call(Subscriber<? super KeyPair> subscriber) {
                String privateKey = fileManager.getStringFromPreferences(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_PRIVATE_KEY);
                String publicKey = fileManager.getStringFromPreferences(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_PUBLIC_KEY);
                KeyPair keyPair = new KeyPair(privateKey, publicKey);

                if (isCached()) {
                    subscriber.onNext(keyPair);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new UserNotFoundException());
                }
            }
        });
    }

    @Override
    public void put(KeyPair keyPair) {
        if (keyPair != null) {
            if (!isCached()) {
                fileManager.writeToPreferences(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_PRIVATE_KEY, keyPair.getPrivateKey());
                fileManager.writeToPreferences(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_PUBLIC_KEY, keyPair.getPublicKey());
            }
        }
    }

    @Override
    public boolean isCached() {
        String privateKey = fileManager.getStringFromPreferences(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_PRIVATE_KEY);
        String publicKey  = fileManager.getStringFromPreferences(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_PUBLIC_KEY);
        return privateKey != null && !privateKey.isEmpty() && publicKey != null && !publicKey.isEmpty();
    }

    @Override
    public void evictAll() {
        executeAsynchronously(new CacheEvictor(fileManager, cacheDir));
    }

    private void executeAsynchronously(Runnable runnable) {
        this.threadExecutor.execute(runnable);
    }

    private static class CacheWriter implements Runnable {
        private final FileManager fileManager;
        private final File fileToWrite;
        private final String fileContent;

        CacheWriter(FileManager fileManager, File fileToWrite, String fileContent) {
            this.fileManager = fileManager;
            this.fileToWrite = fileToWrite;
            this.fileContent = fileContent;
        }

        @Override
        public void run() {
            fileManager.writeToFile(fileToWrite, fileContent);
        }
    }

    private static class CacheEvictor implements Runnable {
        private final FileManager fileManager;
        private final File cacheDir;

        CacheEvictor(FileManager fileManager, File cacheDir) {
            this.fileManager = fileManager;
            this.cacheDir = cacheDir;
        }

        @Override
        public void run() {
            fileManager.clearDirectory(cacheDir);
        }
    }
}
