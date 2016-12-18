package io.soramitsu.irohaandroid.model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import io.soramitsu.irohaandroid.cache.AccountCache;
import io.soramitsu.irohaandroid.cache.FileManager;
import io.soramitsu.irohaandroid.security.KeyStoreManager;

public class Account implements Serializable, AccountCache {
    public String uuid;
    public String alias;
    public List<Asset> assets;

    @Override
    public void save(Context context)
            throws InvalidKeyException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchPaddingException, IOException {

        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();
        String encryptedUuid = keyStoreManager.encrypt(uuid);
        String encryptedAlias = keyStoreManager.encrypt(alias);

        File extStorage = context.getExternalFilesDir("account");
        Log.d("Account soramitsu", "save: " + extStorage.toString());
        File uuidFile = new File(extStorage, "uuid.txt");
        File aliasFile = new File(extStorage, "alias.txt");

        fileManager.writeToFile(uuidFile, encryptedUuid);
        fileManager.writeToFile(aliasFile, encryptedAlias);
    }

    public static String getUuid(Context context)
            throws NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException,
            KeyStoreException, InvalidKeyException, IOException {

        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();

        File extStorage = context.getExternalFilesDir("account");
        File uuidFile = new File(extStorage, "uuid.txt");

        return keyStoreManager.decrypt(fileManager.readFileContent(uuidFile));
    }

    public static String getAlias(Context context)
            throws NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException,
            KeyStoreException, InvalidKeyException, IOException {

        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();

        File extStorage = context.getExternalFilesDir("account");
        File aliasFile = new File(extStorage, "alias.txt");

        return keyStoreManager.decrypt(fileManager.readFileContent(aliasFile));
    }

    public static void delete(Context context) {
        FileManager fileManager = new FileManager();
        fileManager.clearDirectory(context.getExternalFilesDir("account"));
    }
}
