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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.soramitsu.irohaandroid.model.KeyPair;
import io.soramitsu.irohaandroid.security.MessageDigest;

import static io.soramitsu.irohaandroid.security.KeyGenerator.createKeyPair;
import static io.soramitsu.irohaandroid.security.KeyGenerator.sign;
import static io.soramitsu.irohaandroid.security.KeyGenerator.verify;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


/**
 * This is Iroha unit test.<br>
 * Run the Instrumented Unit Test because native method(call C++ function) is not call the Unit Test.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class IrohaTest {

    private KeyPair keyPair;

    @Before
    public void setUp() throws Exception {
        keyPair = createKeyPair();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_sign_Successful() throws Exception {
        final String publicKey = "N1X+Fv7soLknpZNtkdW5cRphgzFjqHmOJl9GvVahWxk=";
        final String privateKey = "aFJfbcedA7p6X0b6EdQNovfFtmq4YSGK/+Bw+XBrsnAEBpXRu+Qfw0559lgLwF2QusChGiDEkLAxPqodQH1kbA==";
        final KeyPair keyPair = new KeyPair(privateKey, publicKey);
        final String message = MessageDigest.digest("test", MessageDigest.Algorithm.SHA3_256);
        final String signature = "bl7EyGwrdDIcHpizHUcDd4Ui34pQRv5VoM69WEPGNveZVOIXJbX3nWhvBvyGXaCxZIuu0THCo5g8PSr2NZJKBg==";

        String result = sign(keyPair, message);

        assertThat(result, is(signature));
    }

    @Test
    public void test_verify_Successful() throws Exception {
        final String publicKey = "N1X+Fv7soLknpZNtkdW5cRphgzFjqHmOJl9GvVahWxk=";
        final String message = MessageDigest.digest("test", MessageDigest.Algorithm.SHA3_256);
        final String signature = "bl7EyGwrdDIcHpizHUcDd4Ui34pQRv5VoM69WEPGNveZVOIXJbX3nWhvBvyGXaCxZIuu0THCo5g8PSr2NZJKBg==";

        boolean result = verify(publicKey, signature, message);

        assertThat(result, is(Boolean.TRUE));
    }

    @Test
    public void test_verify_with_createKeyPair_Successful() throws Exception {
        final String message = "Iroha Android";
        final String signature = sign(keyPair, message);

        boolean result = verify(keyPair.publicKey, signature, message);

        assertThat(result, is(Boolean.TRUE));
    }

    @Test
    public void test_verify_with_sha3_Successful() throws Exception {
        final String message = MessageDigest.digest("Iroha Android", MessageDigest.Algorithm.SHA3_256);
        final String signature = sign(keyPair, message);

        boolean result = verify(keyPair.publicKey, signature, message);

        assertThat(result, is(Boolean.TRUE));
    }

    @Test
    public void test_verify_another_public_key_Failure() throws Exception {
        final String message = "Iroha Android";
        final String signature = sign(keyPair, message);

        final KeyPair anotherKeyPair = createKeyPair();
        boolean result = verify(anotherKeyPair.publicKey, signature, message);

        assertThat(result, is(Boolean.FALSE));
    }

    @Test
    public void test_verify_with_sha3_another_public_key_Failure() throws Exception {
        final String message = MessageDigest.digest("Iroha Android", MessageDigest.Algorithm.SHA3_256);
        final String signature = sign(keyPair, message);

        final KeyPair anotherKeyPair = createKeyPair();
        boolean result = verify(anotherKeyPair.publicKey, signature, message);

        assertThat(result, is(Boolean.FALSE));
    }
}
