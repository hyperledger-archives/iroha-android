package io.soramitsu.irohaandroid.domain.entity;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import io.soramitsu.irohaandroid.data.cache.AccountCache;
import io.soramitsu.irohaandroid.data.cache.FileManager;

public class Account implements Serializable {
    public String uuid;
    public String name;
    public List<Asset> assets;

    public String getUuid(Context context) {
        if (uuid == null || uuid.isEmpty()) {
            FileManager fileManager = new FileManager();
            uuid = fileManager.getStringFromPreferences(
                    context,
                    FileManager.PREFERENCES_FILE_NAME,
                    AccountCache.PREFERENCES_KEY_UUID
            );
        }
        return uuid;
    }
}
