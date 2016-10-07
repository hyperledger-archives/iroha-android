package com.kobaken0029.ed25519;

import android.util.Log;

import org.bouncycastle.util.encoders.Base64;


public class Ed25519 {
    public native static byte[] CreateSeed(byte[] seed);
    public native static ParamObject Ed25519CreateKeyPair(byte[] publicKey, byte[] privateKey, byte[] seed);
    public native static byte[] Ed25519Sign(byte[] signature, byte[] message, byte[] public_key, byte[] private_key);
    public native static int Ed25519Verify(byte[] signature, byte[] message, byte[] public_key);
    public native static byte[] sha3(byte[] message, int length);

    static {
        System.loadLibrary("ed25519-android");
    }

    private static byte[] createSeed() {
        return CreateSeed(new byte[32]);
    }

    public static KeyPair createKeyPair() {
        byte[] publicKey = new byte[32];
        byte[] privateKey = new byte[64];
        byte[] seed = createSeed();
        Log.d("test", "seed created!");
        ParamObject params = Ed25519CreateKeyPair(publicKey, privateKey, seed);
        if (params.getPublicKey() == null || params.getPrivateKey() == null) {
            Log.e("ed25519", "public key or private key are null.");
            return null;
        }
        byte[] encodedPublicKey = Base64.encode(params.getPublicKey());
        byte[] encodedPrivateKey = Base64.encode(params.getPrivateKey());
//        String base64PublicKey = Base64.toBase64String(encodedPublicKey);
//        String base64PrivateKey = Base64.toBase64String(encodedPrivateKey);
        Log.d("test", "[encode] pubkey: " + new String(encodedPublicKey) + "\nprikey: " + new String(encodedPrivateKey));
        return new KeyPair(encodedPublicKey, encodedPrivateKey);
    }

    public static byte[] sign(byte[] publicKey, byte[] privateKey, byte[] message) {
        byte[] signature = new byte[64];
        Log.d("test", "message: " + new String(message));
        byte[] signatureMessage = sha3(message, message.length);
        Log.d("test", "signature message: " + new String(signatureMessage));
        if (publicKey == null || privateKey == null) {
            Log.e("ed25519", "public key or private key are null.");
            return null;
        }
        byte[] decodedPublicKey = Base64.decode(publicKey);
        byte[] decodedPrivateKey = Base64.decode(privateKey);
        Log.d("test", "[decode] pubkey: " + new String(decodedPublicKey) + "\nprikey: " + new String(decodedPrivateKey));
        Log.d("test", "[encode] pubkey: " + new String(Base64.encode(decodedPublicKey)) + "\nprikey: " + new String(Base64.encode(decodedPrivateKey)));
        byte[] sign = Ed25519Sign(signature, signatureMessage, decodedPublicKey, decodedPrivateKey);
        if (sign == null) {
            Log.e("ed25519", "sign is null.");
            return null;
        }
        byte[] encodedSignature = Base64.encode(sign);
        Log.d("test", "[encode] signature: " + new String(encodedSignature));
        return encodedSignature;
    }

    public static int verify(byte[] publicKey, byte[] signature, byte[] message) throws Exception {
        Log.d("test", "message: " + new String(message));
        byte[] signatureMessage = sha3(message, message.length);
        Log.d("test", "signature message: " + new String(signatureMessage));
        if (publicKey == null || signature == null) {
            Log.e("ed25519", "public key or signature are null.");
            return 0;
        }
        byte[] decodedPublicKey = Base64.decode(publicKey);
        byte[] decodedSignature = Base64.decode(signature);
        Log.d("test", "[decode] pubkey: " + new String(decodedPublicKey));
        Log.d("test", "[decode] signature: " + new String(decodedSignature));
        Log.d("test", "[encode] signature: " + new String(Base64.encode(decodedSignature)));
        return Ed25519Verify(decodedSignature, signatureMessage, decodedPublicKey);
    }

    public static class KeyPair {
        private byte[] publicKey;
        private byte[] privateKey;

        public KeyPair(byte[] publicKey, byte[] privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public byte[] getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(byte[] publicKey) {
            this.publicKey = publicKey;
        }

        public byte[] getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(byte[] privateKey) {
            this.privateKey = privateKey;
        }
    }

    public static class ParamObject {
        private byte[] publicKey;
        private byte[] privateKey;
        private byte[] seed;
        private byte[] signature;
        private byte[] message;

        public ParamObject(byte[] seed) {
            this.seed = seed;
        }

        public ParamObject(byte[] publicKey, byte[] privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public ParamObject(byte[] publicKey, byte[] privateKey, byte[] seed) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
            this.seed = seed;
        }

        public ParamObject(byte[] signature, byte[] message, byte[] publicKey, byte[] privateKey) {
            this.signature = signature;
            this.message = message;
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public byte[] getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(byte[] publicKey) {
            this.publicKey = publicKey;
        }

        public byte[] getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(byte[] privateKey) {
            this.privateKey = privateKey;
        }

        public byte[] getSeed() {
            return seed;
        }

        public void setSeed(byte[] seed) {
            this.seed = seed;
        }

        public byte[] getSignature() {
            return signature;
        }

        public void setSignature(byte[] signature) {
            this.signature = signature;
        }

        public byte[] getMessage() {
            return message;
        }

        public void setMessage(byte[] message) {
            this.message = message;
        }
    }
}
