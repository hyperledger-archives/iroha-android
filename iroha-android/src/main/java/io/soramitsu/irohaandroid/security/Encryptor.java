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
// * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.soramitsu.irohaandroid.security;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import io.soramitsu.irohaandroid.exception.IrohaInitializationException;

public class Encryptor {
    public static final String TAG = Encryptor.class.getSimpleName();

    private static final String KEY_ALIAS = "key_alias";

    private static KeyStore.PrivateKeyEntry privateKeyEntry;

    private static boolean isEncryptorInitialized;

    private Encryptor() {
        // make encryptor static class
    }

    @SuppressWarnings("deprecation")
    public static void initialize(Context context) {
        isEncryptorInitialized = true;
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 100);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(KEY_ALIAS)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);
                generator.generateKeyPair();
            }
            privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore
                    .getEntry(KEY_ALIAS, null);
        } catch (Exception e) {
            Log.e(TAG, "encryptor initialization error: ", e);
        }
    }

    public static String encrypt(String string) {
        if (!isEncryptorInitialized) {
            throw new IrohaInitializationException();
        }
        try {
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
            Cipher inCipher = getCipher();
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(string.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "encryption error: ", e);
            return null;
        }
    }

    public static String decrypt(String encrypted) {
        if (!isEncryptorInitialized) {
            throw new IrohaInitializationException();
        }
        try {
            Cipher output = getCipher();

            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(encrypted, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i);
            }
            return new String(bytes, 0, bytes.length, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "decryption error: ", e);
            return null;
        }
    }

    private static Cipher getCipher() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
                return Cipher.getInstance("RSA/ECB/PKCS1Padding",
                        "AndroidOpenSSL"); // error in android 6: InvalidKeyException: Need RSA private or public key
            } else { // android m and above
                return Cipher.getInstance("RSA/ECB/PKCS1Padding",
                        "AndroidKeyStoreBCWorkaround"); // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
            }
        } catch (Exception exception) {
            throw new RuntimeException("getCipher: Failed to get an instance of Cipher", exception);
        }
    }
}