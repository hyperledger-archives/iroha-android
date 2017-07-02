/*
 * Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
 * http://soramitsu.co.jp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.soramitsu.irohaandroid.model;

import android.content.Context;

import java.io.File;

import io.soramitsu.irohaandroid.cache.FileManager;
import io.soramitsu.irohaandroid.cache.KeyPairCache;
import io.soramitsu.irohaandroid.security.KeyStoreManager;

/**
 * KeyPair for ed25519.
 */
public class KeyPair implements KeyPairCache {
    public static final String EXTERNAL_DIRECTORY_NAME = "keypair";
    private static final String EXTERNAL_PRIVATE_KEY_FILE_NAME = "private_key.txt";
    private static final String EXTERNAL_PUBLIC_KEY_FILE_NAME = "public_key.txt";

    public String privateKey;
    public String publicKey;

    public static KeyPair getKeyPair(Context context) {
        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();

        File extStorage = context.getExternalFilesDir(EXTERNAL_DIRECTORY_NAME);
        File privateKeyFile = new File(extStorage, EXTERNAL_PRIVATE_KEY_FILE_NAME);
        File publicKeyFile = new File(extStorage, EXTERNAL_PUBLIC_KEY_FILE_NAME);

        String privateKey = keyStoreManager.decrypt(fileManager.readFileContent(privateKeyFile));
        String publicKey = keyStoreManager.decrypt(fileManager.readFileContent(publicKeyFile));

        return new KeyPair(privateKey, publicKey);
    }

    public KeyPair(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public void save(Context context) {
        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();
        String encryptedPrivateKey = keyStoreManager.encrypt(privateKey);
        String encryptedPublicKey = keyStoreManager.encrypt(publicKey);

        File extStorage = context.getExternalFilesDir(EXTERNAL_DIRECTORY_NAME);
        fileManager.clearDirectory(extStorage);

        File privateKeyFile = new File(extStorage, EXTERNAL_PRIVATE_KEY_FILE_NAME);
        File publicKeyFile = new File(extStorage, EXTERNAL_PUBLIC_KEY_FILE_NAME);

        fileManager.writeToFile(privateKeyFile, encryptedPrivateKey);
        fileManager.writeToFile(publicKeyFile, encryptedPublicKey);
    }

    public static void delete(Context context) {
        FileManager fileManager = new FileManager();
        fileManager.clearDirectory(context.getExternalFilesDir(EXTERNAL_DIRECTORY_NAME));
    }

    public boolean isEmpty() {
        return privateKey == null || publicKey == null || privateKey.isEmpty() || publicKey.isEmpty();
    }
}
