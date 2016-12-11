package io.soramitsu.irohaandroid.model;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import io.soramitsu.irohaandroid.cache.AccountCache;
import io.soramitsu.irohaandroid.cache.FileManager;

import static io.soramitsu.irohaandroid.cache.FileManager.PREFERENCES_FILE_NAME;

public class Account implements Serializable, AccountCache {
    public String uuid;
    public String alias;
    public List<Asset> assets;

    public static String getUuid(Context context) {
        FileManager fileManager = new FileManager();
        return fileManager.getStringFromPreferences(
                context,
                PREFERENCES_FILE_NAME,
                PREFERENCES_KEY_UUID
        );
    }

    public static String getAlias(Context context) {
        FileManager fileManager = new FileManager();
        return fileManager.getStringFromPreferences(
                context,
                PREFERENCES_FILE_NAME,
                PREFERENCES_KEY_ALIAS
        );
    }

    public static void delete(Context context) {
        FileManager fileManager = new FileManager();
        fileManager.removeFromPreferences(
                context, PREFERENCES_FILE_NAME, PREFERENCES_KEY_UUID, PREFERENCES_KEY_ALIAS);
    }

    @Override
    public void save(Context context) {
        FileManager fileManager = new FileManager();
        fileManager.writeToPreferences(
                context,
                PREFERENCES_FILE_NAME,
                PREFERENCES_KEY_UUID,
                uuid
        );
        fileManager.writeToPreferences(
                context,
                PREFERENCES_FILE_NAME,
                PREFERENCES_KEY_ALIAS,
                alias
        );
    }
}
