package io.soramitsu.irohaandroid.util;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.security.MessageDigest;


/**
 * Digest util.
 */
public class DigestUtil {
    private DigestUtil() {
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
