package io.soramitsu.irohaandroid.cache;

import android.content.Context;

public interface KeyPairCache {
    String PREFERENCES_KEY_PUBLIC_KEY = "public_key";
    String PREFERENCES_KEY_PRIVATE_KEY = "private_key";

    void save(Context context);
    void delete(Context context);
}
