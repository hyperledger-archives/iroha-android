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

package io.soramitsu.irohaandroid.security;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

public class KeyStoreManager {
    public static final String TAG = KeyStoreManager.class.getSimpleName();

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "io.soramitsu.iroha.key_alias";
    private static final String CIPHER_ALGO_LARGER_THAN_M = "RSA/ECB/OAEPwithSHA-256andMGF1Padding";
    private static final String CIPHER_ALGO = "RSA/ECB/PKCS1Padding";
    private static final String CHARACTER_CODE_UTF8 = "UTF-8";

    private KeyStore keyStore;
    private Context context;

    private KeyStoreManager(Context context) {
        this.context = context;
        prepareKeyStore();
    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public KeyStoreManager build() {
            return new KeyStoreManager(context);
        }
    }

    private void prepareKeyStore() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            createNewKey();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            Log.e(TAG, "prepareKeyStore: ", e);
        }
    }

    private void createNewKey() {
        try {
            // Create new key if needed
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
                keyPairGenerator.initialize(createKeyPairGeneratorSpec(context));
                keyPairGenerator.generateKeyPair();
            }
        } catch (Exception e) {
            Log.e(TAG, "createNewKey: ", e);
        }
    }

    @SuppressWarnings("deprecation")
    private static AlgorithmParameterSpec createKeyPairGeneratorSpec(Context context) {
        AlgorithmParameterSpec spec;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spec = new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .build();
        } else {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 100);

            spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(KEY_ALIAS)
                    .setSubject(new X500Principal(String.format("CN=%s", KEY_ALIAS)))
                    .setSerialNumber(BigInteger.valueOf(1000000))
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
        }
        return spec;
    }

    private Cipher getCipherInstance() throws NoSuchPaddingException, NoSuchAlgorithmException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Cipher.getInstance(CIPHER_ALGO_LARGER_THAN_M);
        } else {
            return Cipher.getInstance(CIPHER_ALGO);
        }
    }

    public String encrypt(String plainText) {
        try {
            String encryptedText;
            PublicKey publicKey = keyStore.getCertificate(KEY_ALIAS).getPublicKey();

            Cipher cipher = getCipherInstance();
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, cipher);
            cipherOutputStream.write(plainText.getBytes(CHARACTER_CODE_UTF8));
            cipherOutputStream.close();

            byte[] bytes = outputStream.toByteArray();
            encryptedText = Base64.encodeToString(bytes, Base64.DEFAULT);

            return encryptedText;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String decrypt(String encryptedText) {
        try {
            String plainText;
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(KEY_ALIAS, null);

            Cipher cipher = getCipherInstance();
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(encryptedText, Base64.DEFAULT)), cipher);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int b;
            while ((b = cipherInputStream.read()) != -1) {
                outputStream.write(b);
            }
            outputStream.close();
            plainText = outputStream.toString(CHARACTER_CODE_UTF8);

            return plainText;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
