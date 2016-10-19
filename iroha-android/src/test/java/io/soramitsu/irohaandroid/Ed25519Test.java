//package io.soramitsu.irohaandroid;
//
//import junit.framework.TestCase;
//
//import static io.soramitsu.irohaandroid.Ed25519.createKeyPair;
//import static io.soramitsu.irohaandroid.Ed25519.sign;
//import static io.soramitsu.irohaandroid.Ed25519.verify;
//
//
//public class Ed25519Test extends TestCase {
//
//    static {
//        System.loadLibrary("native-lib");
//    }
//
//    public void test_verify_Successful() throws Exception {
//        final KeyPair keyPair = createKeyPair();
//        final String message = "Iroha Android";
//        final String signature = sign(message, keyPair);
//
//        boolean result = verify(signature, message, keyPair.getPublicKey());
//
//        assertTrue(result);
//    }
//}
