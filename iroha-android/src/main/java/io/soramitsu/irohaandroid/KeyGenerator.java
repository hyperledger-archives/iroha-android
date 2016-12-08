package io.soramitsu.irohaandroid;

import io.soramitsu.irohaandroid.model.KeyPair;

public class KeyGenerator {
    private KeyGenerator() {
    }

    public static KeyPair createKeyPair() {
        return Ed25519.createKeyPair();
    }

    public static String sign(KeyPair keyPair, String message) {
        return Ed25519.sign(message, keyPair);
    }

    public static boolean verify(String publicKey, String signature, String message) {
        return Ed25519.verify(signature, message, publicKey);
    }
}
