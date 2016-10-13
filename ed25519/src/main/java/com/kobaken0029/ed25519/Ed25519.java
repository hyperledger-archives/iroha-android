package com.kobaken0029.ed25519;

import android.util.Log;


public class Ed25519 {
    public native static void CreateSeed(int[] seed);
    public native static KeyPair Ed25519CreateKeyPair(int[] publicKey, int[] privateKey, int[] seed);
    public native static void Ed25519Sign(int[] signature, int[] message, int[] public_key, int[] private_key);
    public native static int Ed25519Verify(int[] signature, int[] message, int message_len, int[] public_key);
    public native static void Sha3(int[] message, int length, int[] out);
    public native static int[] Base64Encode(int[] bytes_to_encode, int in_len);
    public native static int[] Base64Decode(int[] encoded_string);

    private static int[] Seed;
    private static KeyPair KeyPair;
    private static int[] Signature;
    private static int[] Sha3Result;

    static {
        System.loadLibrary("ed25519-android");
    }

    private static int[] createSeed() {
        Seed = InitUInt8(32);
        CreateSeed(Seed);
        return Seed;
    }

    public static KeyPair createKeyPair() {
        int[] publicKey = InitUInt8(32);
        int[] privateKey = InitUInt8(64);
        int[] seed = createSeed();
        KeyPair = Ed25519CreateKeyPair(publicKey, privateKey, seed);
        if (KeyPair.getPublicKey() == null || KeyPair.getPrivateKey() == null) {
            Log.e("ed25519", "public key or private key are null.");
            return null;
        }
        Log.d("test", "[encode] pubkey: " + KeyPair.getPublicKey() + "\nprikey: " + KeyPair.getPrivateKey());
        int[] encodedPublicKey = Base64Encode(toInteger(KeyPair.publicKey), toInteger(KeyPair.publicKey).length);
        int[] encodedPrivateKey = Base64Encode(toInteger(KeyPair.privateKey), toInteger(KeyPair.privateKey).length);
        KeyPair = new KeyPair(toString(encodedPublicKey), toString(encodedPrivateKey));
        return KeyPair;
    }

    public static String sign(String publicKey, String privateKey, String message) {
        int[] signature = InitUInt8(64);
        Sha3(toInteger(message), toInteger(message).length, InitUInt8(32));
        int[] signatureMessage = Sha3Result;
        Log.d("test", "message: " + message);
        Log.d("test", "signature message: " + toString(signatureMessage));
        if (publicKey == null || privateKey == null) {
            Log.e("ed25519", "public key or private key are null.");
            return null;
        }
        int[] decodedPublicKey = Base64Decode(toInteger(publicKey));
        int[] decodedPrivateKey = Base64Decode(toInteger(privateKey));
        Log.d("test", "[decode] pubkey: " + toString(decodedPublicKey) + "\nprikey: " + toString(decodedPrivateKey));
        Ed25519Sign(signature, signatureMessage, decodedPublicKey, decodedPrivateKey);
        if (Signature == null) {
            Log.e("ed25519", "sign is null.");
            return null;
        }
        int[] encodedSignature = Base64Encode(Signature, Signature.length);
        Log.d("test", "[encode] signature: " + toString(encodedSignature));
        return toString(encodedSignature);
    }

    public static int verify(String publicKey, String signature, String message) {
        Log.d("test", "[verify] message: " + message);
        Sha3(toInteger(message), toInteger(message).length, InitUInt8(32));
        int[] signatureMessage = Sha3Result;
        Log.d("test", "[verify] signature message: " + toString(signatureMessage));
        if (publicKey == null || signature == null) {
            Log.e("ed25519", "public key or signature are null.");
            return 0;
        }
        int[] decodedPublicKey = Base64Decode(toInteger(publicKey));
        int[] decodedSignature = Base64Decode(toInteger(signature));
        Log.d("test", "[decode] pubkey: " + toString(decodedPublicKey));
        Log.d("test", "[decode] signature: " + toString(decodedSignature));
        return Ed25519Verify(decodedSignature, signatureMessage, signatureMessage.length, decodedPublicKey);
    }

    private static int[] InitUInt8(int length) {
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            byte buff = (byte) 0x00;
            result[i] = buff & 0xFF;
        }
        return result;
    }

   public static String toString(int[] text) {
        StringBuilder out = new StringBuilder();
        for (int i = 0, j = text.length; i < j; i++) {
            out.append(Character.toChars(text[i]));
        }
        return (out.toString());
    }

    public static int[] toInteger(String text) {
        int textLength = text.length();
        for (int i = 0, j = text.length(); i < j; i++) {
            if (Character.isHighSurrogate(text.charAt(i))) {
                textLength--;
                i++;
            }
        }
        int[] out = new int[textLength];
        for (int i = 0, j = text.length(), p = 0; i < j; i++, p++) {
            int c = text.charAt(i);
            if (!Character.isHighSurrogate(text.charAt(i))) {
                out[p] = c;
            } else {
                out[p] = toCodePoint(c, text.charAt(i + 1));
                i++;
            }
        }
        return (out);
    }

    private static int toCodePoint(int high, int low) {
        high -= 0xD800;
        high <<= 10;
        low -= 0xDC00;
        low |= high;
        low += 0x10000;
        return (low);
    }

    @SuppressWarnings("unused")
    private static void setSeed(int[] seed_) {
        Seed = seed_;
    }

    @SuppressWarnings("unused")
    private static void initKeyPair() {
        KeyPair = new KeyPair("", "");
    }

    @SuppressWarnings("unused")
    private static void setPublicKey(String publicKey) {
        KeyPair.publicKey = publicKey;
    }

    @SuppressWarnings("unused")
    private static void setPrivateKey(String privateKey) {
        KeyPair.privateKey = privateKey;
    }

    @SuppressWarnings("unused")
    private static void setSignature(int[] signature_) {
        Signature = signature_;
    }

    @SuppressWarnings("unused")
    private static void setSha3Result(int[] result) {
        Sha3Result = result;
    }

    public static class KeyPair {
        public String publicKey;
        public String privateKey;

        public KeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
    }
}
