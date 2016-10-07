package io.soramitsu.iroha.utils;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.KeyPairGenerator;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;


/**
 * Digest util.
 */
public class DigestUtil {
    private DigestUtil() {
    }

    public static KeyPair createKeyPair_1(Context context) {
        java.security.KeyPairGenerator kpg = null;
        try {
            kpg = java.security.KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            kpg.initialize(createKeyPairGeneratorSpec(context));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return kpg.generateKeyPair();
    }

    public static KeyPairGeneratorSpec createKeyPairGeneratorSpec(Context context) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 100);

        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                .setAlias("iroha_alias")
                .setSubject(new X500Principal(String.format("CN=%s", "iroha_alias")))
                .setSerialNumber(BigInteger.valueOf(1000000))
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();

        return spec;
    }

    public static KeyPair createKeyPair() {
        KeyPairGenerator kpg = new KeyPairGenerator();
        return kpg.generateKeyPair();
    }

    public static String getPublicKeyEncodedBase64(KeyPair keyPair) {
        return getPublicKeyEncodedBase64(keyPair.getPublic());
    }

    public static String getPublicKeyEncodedBase64(PublicKey publicKey) {
        return new String(Base64.encodeBase64(publicKey.getEncoded()));
    }

    public static String sign(PrivateKey privateKey, String message) {
        EdDSAEngine engine = new EdDSAEngine();
        byte[] signature = {};
        try {
            engine.initSign(privateKey);
            engine.update(message.getBytes());
            signature = Base64.encodeBase64(engine.sign());
        } catch (InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return new String(signature);
    }

    public static boolean verify(PublicKey publicKey, String signature, String message) {
        EdDSAEngine engine = new EdDSAEngine();
        boolean verify = false;
        try {
            engine.initVerify(publicKey);
            verify = engine.verify(Base64.decodeBase64(signature));
        } catch (InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return verify;
    }

    public static String sha3_224(final String input) {
        final SHA3.DigestSHA3 sha3 = new SHA3.Digest224();
        sha3.update(input.getBytes());
        return hashToString(sha3);
    }

    public static String sha3_256(final String input) {
        final SHA3.DigestSHA3 sha3 = new SHA3.Digest256();
        sha3.update(input.getBytes());
        return hashToString(sha3);
    }

    public static String sha3_384(final String input) {
        final SHA3.DigestSHA3 sha3 = new SHA3.Digest384();
        sha3.update(input.getBytes());
        return hashToString(sha3);
    }

    public static String sha3_512(final String input) {
        final SHA3.DigestSHA3 sha3 = new SHA3.Digest512();
        sha3.update(input.getBytes());
        return hashToString(sha3);
    }

    private static String hashToString(MessageDigest hash) {
        return hashToString(hash.digest());
    }

    private static String hashToString(byte[] hash) {
        StringBuilder buff = new StringBuilder();

        for (byte b : hash) {
            buff.append(String.format("%02x", b & 0xFF));
        }

        return buff.toString();
    }
}
