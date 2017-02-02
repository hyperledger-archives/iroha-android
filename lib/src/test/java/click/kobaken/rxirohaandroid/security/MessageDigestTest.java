/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

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

package click.kobaken.rxirohaandroid.security;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MessageDigestTest {

    @Test
    public void digest_sha3_256() throws Exception {
        final String message = "";
        final String expected = "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a";

        String result = MessageDigest.digest(message, MessageDigest.Algorithm.SHA3_256);
        assertThat(result, is(expected));
    }

    @Test
    public void digest_sha3_384() throws Exception {
        final String message = "";
        final String expected = "0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004";

        String result = MessageDigest.digest(message, MessageDigest.Algorithm.SHA3_384);
        assertThat(result, is(expected));
    }

    @Test
    public void digest_sha3_512() throws Exception {
        final String message = "";
        final String expected = "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26";

        String result = MessageDigest.digest(message, MessageDigest.Algorithm.SHA3_512);
        assertThat(result, is(expected));
    }

}
