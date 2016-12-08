package io.soramitsu.irohaandroid.model;

import android.content.Context;

import io.soramitsu.irohaandroid.cache.FileManager;
import io.soramitsu.irohaandroid.cache.KeyPairCache;

/**
 * KeyPair for ed25519.
 */
public class KeyPair implements KeyPairCache {
    public String privateKey;
    public String publicKey;

    public static KeyPair getKeyPair(Context context) {
        FileManager fileManager = new FileManager();
        String privateKey = fileManager.getStringFromPreferences(
                context,
                FileManager.PREFERENCES_FILE_NAME,
                KeyPairCache.PREFERENCES_KEY_PRIVATE_KEY
        );
        String publicKey = fileManager.getStringFromPreferences(
                context,
                FileManager.PREFERENCES_FILE_NAME,
                KeyPairCache.PREFERENCES_KEY_PUBLIC_KEY
        );
        return new KeyPair(privateKey, publicKey);
    }

    public KeyPair(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public void save(Context context) {
        FileManager fileManager = new FileManager();
        fileManager.writeToPreferences(
                context,
                FileManager.PREFERENCES_FILE_NAME,
                KeyPairCache.PREFERENCES_KEY_PRIVATE_KEY,
                privateKey
        );
        fileManager.writeToPreferences(
                context,
                FileManager.PREFERENCES_FILE_NAME,
                KeyPairCache.PREFERENCES_KEY_PUBLIC_KEY,
                publicKey
        );
    }

    @Override
    public void delete(Context context) {
        FileManager fileManager = new FileManager();
        fileManager.removeFromPreferences(
                context,
                FileManager.PREFERENCES_FILE_NAME,
                KeyPairCache.PREFERENCES_KEY_PRIVATE_KEY,
                KeyPairCache.PREFERENCES_KEY_PUBLIC_KEY
        );
    }

    public boolean isEmpty() {
        return privateKey == null || publicKey == null || privateKey.isEmpty() || publicKey.isEmpty();
    }
}
