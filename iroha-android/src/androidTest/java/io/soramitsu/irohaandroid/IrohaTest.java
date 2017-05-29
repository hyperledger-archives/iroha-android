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

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.soramitsu.irohaandroid.model.KeyPair;

import static io.soramitsu.irohaandroid.Iroha.createKeyPair;
import static io.soramitsu.irohaandroid.Iroha.sign;
import static io.soramitsu.irohaandroid.Iroha.verify;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


/**
 * This is Iroha unit test.<br>
 * Run the Instrumented Unit Test because native method(call C++ function) is not call the Unit Test.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class IrohaTest {

    private String publicKey;
    private KeyPair keyPair;

    private String plaintextMessage;
    private String sha3Message;

    @Before
    public void setUp() throws Exception {
        publicKey = "N1X+Fv7soLknpZNtkdW5cRphgzFjqHmOJl9GvVahWxk=";
        keyPair = new KeyPair("aFJfbcedA7p6X0b6EdQNovfFtmq4YSGK/+Bw+XBrsnAEBpXRu+Qfw0559lgLwF2QusChGiDEkLAxPqodQH1kbA==", publicKey);
        plaintextMessage = "message";
        sha3Message = "7f4a23d90de90d100754f82d6c14073b7fb466f76fd1f61b187b9f39c3ffd895";
    }

    @Test
    public void test_sign_with_plaintext_Successful() throws Exception {
        final String signature = "jOngFkLJSx+ietTXyAT6PmZWo7LwxZLtulBF8yGTj/QrcsvFK6k9D2h7epSPSXh8cScTVnwF8jAwtJEAYgELDg==";
        assertThat(sign(keyPair, plaintextMessage), is(signature));
    }

    @Test
    public void test_sign_with_sha3_Successful() throws Exception {
        final String signature = "fZtf7CCkCDv7qulpk3ckkVPBRITxslp0mGgEPyBWFv6UFR8bP8CnM4UyZEchJXf6qOCZZda2z9xEs6K7XNwHCw==";
        assertThat(sign(keyPair, sha3Message), is(signature));
    }

    @Test
    public void test_verify_with_plaintext_Successful() throws Exception {
        final String signature = "jOngFkLJSx+ietTXyAT6PmZWo7LwxZLtulBF8yGTj/QrcsvFK6k9D2h7epSPSXh8cScTVnwF8jAwtJEAYgELDg==";
        assertThat(verify(publicKey, signature, plaintextMessage), is(Boolean.TRUE));
    }

    @Test
    public void test_verify_with_sha3_Successful() throws Exception {
        final String signature = "fZtf7CCkCDv7qulpk3ckkVPBRITxslp0mGgEPyBWFv6UFR8bP8CnM4UyZEchJXf6qOCZZda2z9xEs6K7XNwHCw==";
        assertThat(verify(publicKey, signature, sha3Message), is(Boolean.TRUE));
    }

    @Test
    public void test_verify_with_plaintext_and_createKeyPair_Successful() throws Exception {
        final KeyPair keyPair = createKeyPair();
        final String signature = sign(keyPair, plaintextMessage);
        assertThat(verify(keyPair.publicKey, signature, plaintextMessage), is(Boolean.TRUE));
    }

    @Test
    public void test_verify_with_sha3_and_createKeyPair_Successful() throws Exception {
        final KeyPair keyPair = createKeyPair();
        final String signature = sign(keyPair, sha3Message);
        assertThat(verify(keyPair.publicKey, signature, sha3Message), is(Boolean.TRUE));
    }

    @Test
    public void test_verify_with_another_message_Failure() throws Exception {
        final String signature = sign(keyPair, plaintextMessage);
        assertThat(verify(keyPair.publicKey, signature, sha3Message), is(Boolean.FALSE));
    }

    @Test
    public void test_verify_with_plaintext_and_another_public_key_Failure() throws Exception {
        final String signature = sign(keyPair, plaintextMessage);
        final KeyPair anotherKeyPair = createKeyPair();
        assertThat(verify(anotherKeyPair.publicKey, signature, plaintextMessage), is(Boolean.FALSE));
    }

    @Test
    public void test_verify_with_sha3_and_another_public_key_Failure() throws Exception {
        final String signature = sign(keyPair, sha3Message);
        final KeyPair anotherKeyPair = createKeyPair();
        assertThat(verify(anotherKeyPair.publicKey, signature, sha3Message), is(Boolean.FALSE));
    }
}
