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

package io.soramitsu.irohaandroid;

import java.util.ArrayList;
import java.util.List;

import io.soramitsu.irohaandroid.model.KeyPair;

/**
 * This class can be generate keypair or create signature or verify the message.
 */
public class Ed25519 {
    private native static ArrayList<String> GenerateKeyPair();
    private native static String Signature(String message, String priKey, String pubKey);
    private native static boolean Verify(String signatureb64, String message, String pubKeyb64);

    static {
        System.loadLibrary("native-lib");
    }

    /**
     * Create KeyPair.
     *
     * @return KeyPair (public key and private key are encoded by base64)
     */
    public static KeyPair createKeyPair() {
        List<String> generatedKeyPair = GenerateKeyPair();
        return new KeyPair(generatedKeyPair.get(0), generatedKeyPair.get(1));
    }

    /**
     * Create signature from the message with KeyPair.
     *
     * @param message target message
     * @param keyPair using converted to signature
     * @return signature
     */
    public static String sign(String message, KeyPair keyPair) {
        return Signature(message, keyPair.privateKey, keyPair.publicKey);
    }

    /**
     * Check the message by signature.
     *
     * @param signature signature (encoded by base64)
     * @param message   target message
     * @param publicKey ed25519 public key (encoded by base64)
     * @return true if the correct message
     */
    public static boolean verify(String signature, String message, String publicKey) {
        return Verify(signature, message, publicKey);
    }
}
