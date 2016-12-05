package io.soramitsu.irohaandroid.domain.entity;

/**
 * KeyPair for ed25519.
 */
public class KeyPair {
    private String privateKey;
    private String publicKey;

    public KeyPair(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public boolean isEmpty() {
        return privateKey == null || publicKey == null || privateKey.isEmpty() || publicKey.isEmpty();
    }
}
