package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.soramitsu.iroha.models.IrohaUser;
import io.soramitsu.iroha.models.ResponseObject;
import io.soramitsu.iroha.utils.NetworkMockUtil;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(JUnit4.class)
public class BaseClientTest extends TestCase {
    private static final int TIMEOUT = 10000;

    private BaseClient userClient;

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
                    case "/get":
                        return new MockResponse().setBody("{\n" +
                                "  \"message\": \"successful\",\n" +
                                "  \"status\": 200\n" +
                                "}").setResponseCode(200);
                    case "/post":
                        return new MockResponse().setBody("{\n" +
                                "  \"message\": \"successful\",\n" +
                                "  \"status\": 200\n" +
                                "}").setResponseCode(200);
                    case "/bad_request":
                        return new MockResponse().setBody("{\n" +
                                "  \"message\": \"failed\",\n" +
                                "  \"status\": 400\n" +
                                "}").setResponseCode(400);
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

    @Before
    public void setUp() throws Exception {
        userClient = new BaseClient(new OkHttpClient(), new Gson());
    }

    @Test(timeout = TIMEOUT)
    public void testGet_Successful() throws Exception {
        fail();
    }

    @Test(timeout = TIMEOUT)
    public void testGet_Failure() throws Exception {
        fail();
    }

    @Test(timeout = TIMEOUT)
    public void testPost_Successful() throws Exception {
        fail();
    }

    @Test(timeout = TIMEOUT)
    public void testPost_Failure() throws Exception {
        fail();
    }
}
