package io.soramitsu.irohaandroid.model;

import android.content.Context;

import java.io.File;
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

        File extStorage = context.getExternalFilesDir("keypair");
        File privateKeyFile = new File(extStorage, "private_key.txt");
        File publicKeyFile = new File(extStorage, "public_key.txt");

        String privateKey = keyStoreManager.decrypt(fileManager.readFileContent(privateKeyFile));
        String publicKey = keyStoreManager.decrypt(fileManager.readFileContent(publicKeyFile));

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

        File extStorage = context.getExternalFilesDir("keypair");
        File privateKeyFile = new File(extStorage, "private_key.txt");
        File publicKeyFile = new File(extStorage, "public_key.txt");

        fileManager.writeToFile(privateKeyFile, encryptedPrivateKey);
        fileManager.writeToFile(publicKeyFile, encryptedPublicKey);
    }

    public static void delete(Context context) {
        FileManager fileManager = new FileManager();
        fileManager.clearDirectory(context.getExternalFilesDir("keypair"));
    }

    public boolean isEmpty() {
        return privateKey == null || publicKey == null || privateKey.isEmpty() || publicKey.isEmpty();
    }
}
