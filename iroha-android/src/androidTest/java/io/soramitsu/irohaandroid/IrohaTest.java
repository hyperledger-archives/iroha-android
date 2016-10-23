package io.soramitsu.irohaandroid;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.soramitsu.irohaandroid.Ed25519.createKeyPair;
import static io.soramitsu.irohaandroid.Ed25519.sign;
import static io.soramitsu.irohaandroid.Ed25519.verify;
import static io.soramitsu.irohaandroid.Iroha.sha3_256;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


/**
 * native method is only test.
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
        final String message = sha3_256("test");
        final String signature = "Y6sgwsm2GEr//KI7uDUe+IFoiOQt7WNsLxn15A40pUsvYAeVMxb7koxGA1Uixxfzwm7hbX5ke/MYboTdMfOzBA==";

        String result = Iroha.sign(keyPair, message);

        assertThat(result, is(signature));
    }

    @Test
    public void test_verify_Successful() throws Exception {
        final String publicKey = "N1X+Fv7soLknpZNtkdW5cRphgzFjqHmOJl9GvVahWxk=";
        final String message = sha3_256("test");
        final String signature = "Y6sgwsm2GEr//KI7uDUe+IFoiOQt7WNsLxn15A40pUsvYAeVMxb7koxGA1Uixxfzwm7hbX5ke/MYboTdMfOzBA==";

        boolean result = Iroha.verify(publicKey, signature, message);

        assertThat(result, is(Boolean.TRUE));
    }

    @Test
    public void test_verify_with_createKeyPair_Successful() throws Exception {
        final String message = "Iroha Android";
        final String signature = sign(message, keyPair);

        boolean result = verify(signature, message, keyPair.getPublicKey());

        assertThat(result, is(Boolean.TRUE));
    }

    @Test
    public void test_verify_with_sha3_Successful() throws Exception {
        final String message = sha3_256("Iroha Android");
        final String signature = sign(message, keyPair);

        boolean result = verify(signature, message, keyPair.getPublicKey());

        assertThat(result, is(Boolean.TRUE));
    }

    @Test
    public void test_verify_another_public_key_Failure() throws Exception {
        final String message = "Iroha Android";
        final String signature = sign(message, keyPair);

        final KeyPair anotherKeyPair = createKeyPair();
        boolean result = verify(signature, message, anotherKeyPair.getPublicKey());

        assertThat(result, is(Boolean.FALSE));
    }

    @Test
    public void test_verify_with_sha3_another_public_key_Failure() throws Exception {
        final String message = sha3_256("Iroha Android");
        final String signature = sign(message, keyPair);

        final KeyPair anotherKeyPair = createKeyPair();
        boolean result = verify(signature, message, anotherKeyPair.getPublicKey());

        assertThat(result, is(Boolean.FALSE));
    }
}
