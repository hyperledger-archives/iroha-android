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

import android.support.annotation.NonNull;

import org.bouncycastle.jcajce.provider.digest.SHA3;

public class MessageDigest {
    private MessageDigest() {
    }

    public enum Algorithm {
        SHA3_256, SHA3_384, SHA3_512
    }

    public static String digest(String message, @NonNull Algorithm algorithm) {
        switch (algorithm) {
            case SHA3_256:
                return sha3_256(message);
            case SHA3_384:
                return sha3_384(message);
            case SHA3_512:
                return sha3_512(message);
            default:
                return sha3_256(message);
        }
    }

    private static String sha3_256(final String message) {
        return sha3_x(new SHA3.Digest256(), message);
    }

    private static String sha3_384(final String message) {
        return sha3_x(new SHA3.Digest384(), message);
    }

    private static String sha3_512(final String message) {
        return sha3_x(new SHA3.Digest512(), message);
    }

    private static String sha3_x(SHA3.DigestSHA3 sha3, String message) {
        sha3.update(message.getBytes());
        return hashToString(sha3);
    }

    private static String hashToString(java.security.MessageDigest hash) {
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
