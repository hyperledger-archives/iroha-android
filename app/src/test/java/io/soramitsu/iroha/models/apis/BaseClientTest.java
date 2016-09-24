package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.soramitsu.iroha.models.ResponseObject;
import io.soramitsu.iroha.utils.NetworkMockUtil;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static io.soramitsu.iroha.utils.JsonParserUtil.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(JUnit4.class)
public class BaseClientTest extends TestCase {
    private static final int TIMEOUT = 10000;

    private BaseClient baseClient = new BaseClient(new OkHttpClient(), new Gson());

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
                        if (request.getMethod().equals("POST")) {
                            return new MockResponse().setBody("{\n" +
                                    "  \"message\": \"successful\",\n" +
                                    "  \"status\": 200\n" +
                                    "}").setResponseCode(200);
                        }
                        return new MockResponse().setResponseCode(404);
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
        baseClient = new BaseClient(new OkHttpClient(), new Gson());
    }

    @Test(timeout = TIMEOUT)
    public void testGet_Successful() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/get");
        final Response response = baseClient.get(url.toString());
        final ResponseObject result = (ResponseObject) parse(response.body().string(), ResponseObject.class);

        assertThat(result.getStatus(), is(BaseClient.STATUS_OK));
    }

    @Test(timeout = TIMEOUT)
    public void testGet_Failure() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/bad_request");
        final Response response = baseClient.get(url.toString());
        final ResponseObject result = (ResponseObject) parse(response.body().string(), ResponseObject.class);

        assertThat(result.getStatus(), is(BaseClient.STATUS_BAD));
    }

    @Test(timeout = TIMEOUT)
    public void testGet_NotFound() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/nothing");
        final Response response = baseClient.get(url.toString());

        assertThat(response.code(), is(BaseClient.STATUS_NOT_FOUND));
    }

    @Test(timeout = TIMEOUT)
    public void testPost_Successful() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/post");
        final Response response = baseClient.post(url.toString(), "{\"key:\"\"value\"}");
        final ResponseObject result = (ResponseObject) parse(response.body().string(), ResponseObject.class);

        assertThat(result.getStatus(), is(BaseClient.STATUS_OK));
    }

    @Test(timeout = TIMEOUT)
    public void testPost_Failure() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/bad_request");
        final Response response = baseClient.post(url.toString(), "{\"key:\"\"value\"}");
        final ResponseObject result = (ResponseObject) parse(response.body().string(), ResponseObject.class);

        assertThat(result.getStatus(), is(BaseClient.STATUS_BAD));
    }

    @Test(timeout = TIMEOUT)
    public void testPost_NotFound() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/nothing");
        final Response response = baseClient.post(url.toString(), "{\"key:\"\"value\"}");

        assertThat(response.code(), is(BaseClient.STATUS_NOT_FOUND));
    }
}
