package io.soramitsu.irohaandroid.cache;

import android.content.Context;

public interface AccountCache {
    String PREFERENCES_KEY_UUID = "uuid";
    String PREFERENCES_KEY_ALIAS = "alias";

    void save(Context context);
}
