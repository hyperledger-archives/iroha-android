package io.soramitsu.irohaandroid.data.cache;

import android.content.Context;

import java.io.File;

import io.soramitsu.irohaandroid.data.cache.serializer.JsonSerializer;
import io.soramitsu.irohaandroid.data.entity.AccountEntity;
import io.soramitsu.irohaandroid.data.exception.UserNotFoundException;
import io.soramitsu.irohaandroid.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;

public class AccountCacheImpl implements AccountCache {
    private static final String SETTINGS_FILE_NAME = "io.soramitsu.irohaandroid.SETTINGS";
    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";

    private static final String DEFAULT_FILE_NAME = "user_";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;

    private final Context context;
    private final File cacheDir;
    private final JsonSerializer serializer;
    private final FileManager fileManager;
    private final ThreadExecutor threadExecutor;

    public AccountCacheImpl(Context context, JsonSerializer serializer,
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
    public Observable<String> get() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String uuid = fileManager.getStringFromPreferences(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_UUID);

                if (!uuid.isEmpty()) {
                    subscriber.onNext(uuid);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new UserNotFoundException());
                }
            }
        });
    }

    @Override
    public Observable<AccountEntity> get(final String uuid) {
        return Observable.create(new Observable.OnSubscribe<AccountEntity>() {
            @Override
            public void call(Subscriber<? super AccountEntity> subscriber) {
                File userEntityFile = buildFile(uuid);
                String fileContent = fileManager.readFileContent(userEntityFile);
                AccountEntity accountEntity = serializer.deserializeToAccountEntity(fileContent);

                if (accountEntity != null) {
                    subscriber.onNext(accountEntity);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new UserNotFoundException());
                }
            }
        });
    }

    @Override
    public void put(AccountEntity accountEntity) {
        if (accountEntity != null) {
            String uuid = fileManager.getStringFromPreferences(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_UUID);
            if (!uuid.isEmpty()) {
                File userEntityFile = buildFile(uuid);
                if (!isCached(uuid)) {
                    String jsonString = serializer.serialize(accountEntity);
                    this.executeAsynchronously(new CacheWriter(fileManager, userEntityFile, jsonString));
                    setLastCacheUpdateTimeMillis();
                }
            }
        }
    }

    @Override
    public void put(String uuid, AccountEntity accountEntity) {
        if (accountEntity != null) {
            fileManager.writeToPreferences(context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_UUID, uuid);
            File userEntityFile = buildFile(uuid);
            if (!isCached(uuid)) {
                String jsonString = serializer.serialize(accountEntity);
                executeAsynchronously(new CacheWriter(fileManager, userEntityFile, jsonString));
                setLastCacheUpdateTimeMillis();
            }
        }
    }

    @Override
    public boolean isCached(String uuid) {
        File userEntityFile = buildFile(uuid);
        return this.fileManager.exists(userEntityFile);
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

    private File buildFile(String uuid) {
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(cacheDir.getPath());
        fileNameBuilder.append(File.separator);
        fileNameBuilder.append(DEFAULT_FILE_NAME);
        fileNameBuilder.append(uuid);

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
