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

import io.soramitsu.irohaandroid.Ed25519;
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
