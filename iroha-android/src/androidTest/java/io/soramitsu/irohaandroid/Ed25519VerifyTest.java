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
import static io.soramitsu.irohaandroid.util.DigestUtil.sha3_256;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class Ed25519VerifyTest {

    private KeyPair keyPair;

    @Before
    public void setUp() throws Exception {
        keyPair = createKeyPair();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_verify_Successful() throws Exception {
        final String message = "Iroha Android";
        final String signature = sign(message, keyPair);

        boolean result = verify(signature, message, keyPair.getPublicKey());

        assertThat(Boolean.TRUE, is(result));
    }

    @Test
    public void test_verify_with_sha3_Successful() throws Exception {
        final String message = sha3_256("Iroha Android");
        final String signature = sign(message, keyPair);

        boolean result = verify(signature, message, keyPair.getPublicKey());

        assertThat(Boolean.TRUE, is(result));
    }

    @Test
    public void test_verify_another_public_key_Failure() throws Exception {
        final String message = sha3_256("Iroha Android");
        final String signature = sign(message, keyPair);

        final KeyPair anotherKeyPair = createKeyPair();
        boolean result = verify(signature, message, anotherKeyPair.getPublicKey());

        assertThat(Boolean.FALSE, is(result));
    }

    @Test
    public void test_verify_with_sha3_another_public_key_Failure() throws Exception {
        final String message = sha3_256("Iroha Android");
        final String signature = sign(message, keyPair);

        final KeyPair anotherKeyPair = createKeyPair();
        boolean result = verify(signature, message, anotherKeyPair.getPublicKey());

        assertThat(Boolean.FALSE, is(result));
    }
}
