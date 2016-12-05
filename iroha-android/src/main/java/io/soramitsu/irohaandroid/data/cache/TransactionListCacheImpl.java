package io.soramitsu.irohaandroid.data.cache;

import android.content.Context;

import java.io.File;

import io.soramitsu.irohaandroid.data.cache.serializer.JsonSerializer;
import io.soramitsu.irohaandroid.data.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.data.exception.UserNotFoundException;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;

public class TransactionListCacheImpl implements TransactionListCache {
    private static final String SETTINGS_FILE_NAME = "io.soramitsu.irohaandroid.SETTINGS";
    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";

    private static final String DEFAULT_FILE_NAME = "transactions_";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;

    private final Context context;
    private final File cacheDir;
    private final JsonSerializer serializer;
    private final FileManager fileManager;
    private final ThreadExecutor threadExecutor;

    public TransactionListCacheImpl(Context context, JsonSerializer serializer,
                                    FileManager fileManager, ThreadExecutor threadExecutor) {
        if (context == null || serializer == null || fileManager == null || threadExecutor == null) {
            throw new IllegalArgumentException("Invalid null parameter");
        }
        this.context = context;
        this.cacheDir = context.getCacheDir();
        this.serializer = serializer;
        this.fileManager = fileManager;
        this.threadExecutor = threadExecutor;
    }

    @Override
    public Observable<TransactionListEntity> get(final String uuid) {
        return Observable.create(new Observable.OnSubscribe<TransactionListEntity>() {
            @Override
            public void call(Subscriber<? super TransactionListEntity> subscriber) {
                File userEntityFile = buildFile(uuid);
                String fileContent = fileManager.readFileContent(userEntityFile);
                TransactionListEntity transactionListEntity = serializer.deserializeToTransactionListEntity(fileContent);

                if (transactionListEntity != null) {
                    subscriber.onNext(transactionListEntity);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new UserNotFoundException());
                }
            }
        });
    }

    @Override
    public Observable<TransactionListEntity> get(final String uuid, final String domain, final String asset) {
        return Observable.create(new Observable.OnSubscribe<TransactionListEntity>() {
            @Override
            public void call(Subscriber<? super TransactionListEntity> subscriber) {
                File transactionListEntityFile = buildFile(uuid, domain, asset);
                String fileContent = fileManager.readFileContent(transactionListEntityFile);
                TransactionListEntity transactionListEntity = serializer.deserializeToTransactionListEntity(fileContent);

                if (transactionListEntity != null) {
                    subscriber.onNext(transactionListEntity);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new UserNotFoundException());
                }
            }
        });
    }

    @Override
    public void put(String uuid, TransactionListEntity transactionListEntity) {
        if (transactionListEntity != null) {
            File transactionListEntityFile = buildFile(uuid);
            if (!isCached(uuid)) {
                String jsonString = serializer.serialize(transactionListEntity);
                executeAsynchronously(new CacheWriter(fileManager, transactionListEntityFile, jsonString));
                setLastCacheUpdateTimeMillis();
            }
        }
    }

    @Override
    public void put(String uuid, String domain, String asset, TransactionListEntity transactionListEntity) {
        if (transactionListEntity != null) {
            File transactionListEntityFile = buildFile(uuid, domain, asset);
            if (!isCached(uuid, domain, asset)) {
                String jsonString = serializer.serialize(transactionListEntity);
                executeAsynchronously(new CacheWriter(fileManager, transactionListEntityFile, jsonString));
                setLastCacheUpdateTimeMillis();
            }
        }
    }

    @Override
    public boolean isCached(String uuid, String... other) {
        File transactionListEntityFile = buildFile(uuid, other);
        return this.fileManager.exists(transactionListEntityFile);
    }

    @Override
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long lastUpdateTime = getLastCacheUpdateTimeMillis();

        boolean expired = ((currentTime - lastUpdateTime) > EXPIRATION_TIME);

        if (expired) {
            this.evictAll();
        }

        return expired;
    }

    @Override
    public void evictAll() {
        executeAsynchronously(new CacheEvictor(fileManager, cacheDir));
    }

    private File buildFile(String uuid, String... other) {
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(cacheDir.getPath());
        fileNameBuilder.append(File.separator);
        fileNameBuilder.append(DEFAULT_FILE_NAME);
        fileNameBuilder.append(uuid);
        if (other != null && other.length == 2 && other[0] != null && other[1] != null) {
            fileNameBuilder.append(other[0]);
            fileNameBuilder.append(other[1]);
        }

        return new File(fileNameBuilder.toString());
    }

    private void setLastCacheUpdateTimeMillis() {
        long currentMillis = System.currentTimeMillis();
        this.fileManager.writeToPreferences(context, SETTINGS_FILE_NAME,
                SETTINGS_KEY_LAST_CACHE_UPDATE, currentMillis);
    }

    private long getLastCacheUpdateTimeMillis() {
        return this.fileManager.getLongFromPreferences(context, SETTINGS_FILE_NAME,
                SETTINGS_KEY_LAST_CACHE_UPDATE);
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
