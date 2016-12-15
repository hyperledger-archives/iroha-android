package io.soramitsu.irohaandroid.cache;

import android.content.Context;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

public interface KeyPairCache {
    String PREFERENCES_KEY_PUBLIC_KEY = "public_key";
    String PREFERENCES_KEY_PRIVATE_KEY = "private_key";

    void save(Context context)
            throws InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchPaddingException, IOException;
    void delete(Context context);
}
