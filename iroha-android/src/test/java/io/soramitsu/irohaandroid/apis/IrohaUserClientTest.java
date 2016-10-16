package io.soramitsu.irohaandroid.apis;

import com.kobaken0029.ed25519.Ed25519;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.PublicKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.soramitsu.irohaandroid.models.IrohaUser;
import io.soramitsu.irohaandroid.models.apis.IrohaUserClient;
import io.soramitsu.irohaandroid.utils.NetworkMockUtil;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static com.kobaken0029.ed25519.Ed25519.createKeyPair;
import static io.soramitsu.irohaandroid.utils.NetworkMockUtil.call;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class IrohaUserClientTest extends TestCase {
    private static final int TIMEOUT = 10000;

    private static final String TEST_EXIST_UUID = "1234";
    private static final String TEST_NO_EXIST_UUID = "5678";

    private IrohaUserClient userClient = IrohaUserClient.newInstance();

    @BeforeClass
    public static void setUpOnce() throws Exception {
        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(final RecordedRequest request)
                    throws InterruptedException {
                if (request == null || request.getPath() == null) {
                    return new MockResponse().setResponseCode(400);
                }
                switch (request.getPath()) {
                    case "//account/register":
                        if (request.getMethod().equals("POST")) {
                            Pattern p = Pattern.compile("\"alias\":\"(.+?)\"");
                            Matcher m = p.matcher(request.getBody().toString());
                            if (m.find() && m.group(1).equals("duplicate_user")) {
                                return new MockResponse().setBody("{\n" +
                                        "  \"message\": \"duplicate user\",\n" +
                                        "  \"status\": 400\n" +
                                        "}").setResponseCode(400);
                            }
                            return new MockResponse().setBody("{\n" +
                                    "  \"message\": \"successful\",\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"uuid\": \"" + TEST_EXIST_UUID + "\"\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "//account?uuid=" + TEST_EXIST_UUID:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"alias\": \"user_name\",\n" +
                                    "  \"assets\": [{\n" +
                                    "    \"name\" : \"moeka\",\n" +
                                    "    \"domain\" : \"soramitsu\",\n" +
                                    "    \"value\" : \"100\"\n" +
                                    "  },{\n" +
                                    "    \"name\" : \"moeka\",\n" +
                                    "    \"domain\" : \"soramitsu\",\n" +
                                    "    \"value\" : \"Owner\"\n" +
                                    "  }]\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(400);
                    case "//account?uuid=" + TEST_NO_EXIST_UUID:
                        if (request.getMethod().equals("GET")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"message\": \"User not found!\",\n" +
                                    "  \"status\": 400\n" +
                                    "}").setResponseCode(400);
                        }
                        return new MockResponse().setResponseCode(400);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };
        NetworkMockUtil.createMockWebServer(dispatcher);
    }

    @AfterClass
    public static void tearDownOnce() throws Exception {
        NetworkMockUtil.shutdownMockWebServer();
    }

    @Test(timeout = TIMEOUT)
    public void testRegister_Successful() throws Exception {
        final String publicKey = "public_key";
        final String alias = "user_name";

        final IrohaUser result = userClient.register(call(""), publicKey, alias);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getName(), is(alias));
        assertThat(result.getUuid(), is(TEST_EXIST_UUID));
    }

    @Test(timeout = TIMEOUT)
    public void testRegister_Duplicate() throws Exception {
        final String publicKey = "public_key";
        final String alias = "duplicate_user";

        final IrohaUser result = userClient.register(call(""), publicKey, alias);

        assertThat(result.getStatus(), is(400));
        assertThat(result.getMessage(), is("duplicate user"));
    }

    @Test(timeout = TIMEOUT)
    public void testRegister_NotFound() throws Exception {
        final String publicKey = "public_key";
        final String alias = "user_name";

        final IrohaUser result = userClient.register(call("/nothing"), publicKey, alias);

        assertThat(result.getStatus(), is(404));
    }

    @Test(timeout = TIMEOUT)
    public void testFindUserInfo_Successful() throws Exception {
        final String screenName = "user_name";

        final IrohaUser result = userClient.findAccountInfo(call(""), TEST_EXIST_UUID);

        assertThat(result.getStatus(), is(200));
        assertThat(result.getName(), is(screenName));
        assertThat(result.getAssets().size(), is(2));
        assertThat(result.getAssets().get(0).getValue(), is("100"));
        assertThat(result.getAssets().get(1).getValue(), is("Owner"));
    }

    @Test(timeout = TIMEOUT)
    public void testFindUserInfo_UserNotFound() throws Exception {
        final IrohaUser result = userClient.findAccountInfo(call(""), TEST_NO_EXIST_UUID);
        assertThat(result.getStatus(), is(400));
    }

    @Test(timeout = TIMEOUT)
    public void testFindUserInfo_NotFound() throws Exception {
        final IrohaUser result = userClient.findAccountInfo(call("/nothing"), TEST_EXIST_UUID);
        assertThat(result.getStatus(), is(404));
    }
}
