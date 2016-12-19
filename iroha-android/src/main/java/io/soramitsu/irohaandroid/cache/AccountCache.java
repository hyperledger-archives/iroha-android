package io.soramitsu.irohaandroid.cache;

import android.content.Context;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

public interface AccountCache {
    String PREFERENCES_KEY_UUID = "uuid";
    String PREFERENCES_KEY_ALIAS = "alias";

    void save(Context context)
            throws InvalidKeyException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchPaddingException, IOException;
}
