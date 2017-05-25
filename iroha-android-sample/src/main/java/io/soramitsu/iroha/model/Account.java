/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.soramitsu.iroha.model;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import io.soramitsu.irohaandroid.cache.FileManager;
import io.soramitsu.irohaandroid.security.KeyStoreManager;

public class Account implements Serializable {
    public String uuid;
    public String alias;
    public List<Asset> assets;

    public void save(Context context) {
        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();
        String encryptedUuid = keyStoreManager.encrypt(uuid);
        String encryptedAlias = keyStoreManager.encrypt(alias);

        File extStorage = context.getExternalFilesDir("account");
        fileManager.clearDirectory(extStorage);

        File uuidFile = new File(extStorage, "uuid.txt");
        File aliasFile = new File(extStorage, "alias.txt");

        fileManager.writeToFile(uuidFile, encryptedUuid);
        fileManager.writeToFile(aliasFile, encryptedAlias);
    }

    public static String getUuid(Context context) {
        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();

        File extStorage = context.getExternalFilesDir("account");
        File uuidFile = new File(extStorage, "uuid.txt");

        return keyStoreManager.decrypt(fileManager.readFileContent(uuidFile));
    }

    public static String getAlias(Context context) {
        FileManager fileManager = new FileManager();

        KeyStoreManager keyStoreManager = new KeyStoreManager.Builder(context).build();

        File extStorage = context.getExternalFilesDir("account");
        File aliasFile = new File(extStorage, "alias.txt");

        return keyStoreManager.decrypt(fileManager.readFileContent(aliasFile));
    }

    public static void delete(Context context) {
        FileManager fileManager = new FileManager();
        fileManager.clearDirectory(context.getExternalFilesDir("keypair"));
        fileManager.clearDirectory(context.getExternalFilesDir("account"));
    }
}
