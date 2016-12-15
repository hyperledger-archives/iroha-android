package io.soramitsu.irohaandroid.model;

import android.content.Context;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.crypto.NoSuchPaddingException;

import io.soramitsu.irohaandroid.cache.FileManager;
import io.soramitsu.irohaandroid.cache.KeyPairCache;
import io.soramitsu.irohaandroid.security.KeyStoreManager;

/**
 * KeyPair for ed25519.
 */
public class KeyPair implements KeyPairCache {
    public String privateKey;
    public String publicKey;

    public static KeyPair getKeyPair(Context context)
            throws NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException,
            KeyStoreException, InvalidKeyException, IOException {
        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();

        String privateKey = keyStoreManager.decrypt(fileManager.readFileContent(context.getExternalFilesDir("private_key.txt")));
        String publicKey = keyStoreManager.decrypt(fileManager.readFileContent(context.getExternalFilesDir("public_key.txt")));

//        String privateKey = fileManager.getStringFromPreferences(
//                context,
//                FileManager.PREFERENCES_FILE_NAME,
//                KeyPairCache.PREFERENCES_KEY_PRIVATE_KEY
//        );
//        String publicKey = fileManager.getStringFromPreferences(
//                context,
//                FileManager.PREFERENCES_FILE_NAME,
//                KeyPairCache.PREFERENCES_KEY_PUBLIC_KEY
//        );
        return new KeyPair(privateKey, publicKey);
    }

    public KeyPair(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public void save(Context context)
            throws InvalidKeyException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchPaddingException, IOException {
        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();
        String encryptedPrivateKey = keyStoreManager.encrypt(privateKey);
        String encryptedPublicKey = keyStoreManager.encrypt(publicKey);

        fileManager.writeToFile(context.getExternalFilesDir("private_key.txt"), encryptedPrivateKey);
        fileManager.writeToFile(context.getExternalFilesDir("public_key.txt"), encryptedPublicKey);
//        fileManager.writeToPreferences(
//                context,
//                FileManager.PREFERENCES_FILE_NAME,
//                KeyPairCache.PREFERENCES_KEY_PRIVATE_KEY,
//                privateKey
//        );
//        fileManager.writeToPreferences(
//                context,
//                FileManager.PREFERENCES_FILE_NAME,
//                KeyPairCache.PREFERENCES_KEY_PUBLIC_KEY,
//                publicKey
//        );
    }

    @Override
    public void delete(Context context) {
        FileManager fileManager = new FileManager();
        fileManager.clearDirectory(context.getExternalFilesDir("private_key.txt"));
        fileManager.clearDirectory(context.getExternalFilesDir("public_key.txt"));
//        fileManager.removeFromPreferences(
//                context,
//                FileManager.PREFERENCES_FILE_NAME,
//                KeyPairCache.PREFERENCES_KEY_PRIVATE_KEY,
//                KeyPairCache.PREFERENCES_KEY_PUBLIC_KEY
//        );
    }

    public boolean isEmpty() {
        return privateKey == null || publicKey == null || privateKey.isEmpty() || publicKey.isEmpty();
    }
}
