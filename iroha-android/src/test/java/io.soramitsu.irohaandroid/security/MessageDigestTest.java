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

import org.junit.Test;

import static io.soramitsu.irohaandroid.security.MessageDigest.Algorithm.SHA3_256;
import static io.soramitsu.irohaandroid.security.MessageDigest.Algorithm.SHA3_384;
import static io.soramitsu.irohaandroid.security.MessageDigest.Algorithm.SHA3_512;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit test for MessageDigest
 */
public class MessageDigestTest {

    private String message;

    public MessageDigestTest() {
        this.message = "test";
    }

    @Test
    public void digest_sha3_256() throws Exception {
        final String expected = "36f028580bb02cc8272a9a020f4200e346e276ae664e45ee80745574e2f5ab80";
        assertThat(MessageDigest.digest(message, SHA3_256), is(expected));
    }

    @Test
    public void digest_sha3_384() throws Exception {
        final String expected = "e516dabb23b6e30026863543282780a3ae0dccf05551cf0295178d7ff0f1b41eecb9db3ff219007c4e097260d58621bd";
        assertThat(MessageDigest.digest(message, SHA3_384), is(expected));
    }

    @Test
    public void digest_sha3_512() throws Exception {
        final String expected = "9ece086e9bac491fac5c1d1046ca11d737b92a2b2ebd93f005d7b710110c0a678288166e7fbe796883a4f2e9b3ca9f484f521d0ce464345cc1aec96779149c14";
        assertThat(MessageDigest.digest(message, SHA3_512), is(expected));
    }
}
