package io.soramitsu.iroha.utils;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.soramitsu.iroha.models.ResponseObject;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static io.soramitsu.iroha.utils.JsonParserUtil.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(JUnit4.class)
public class NetworkUtilTest extends TestCase {
    private static final int TIMEOUT = 10000;

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

    @Test(timeout = TIMEOUT)
    public void testGet_Successful() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/get");
        final Response response = NetworkUtil.get(url.toString());
        final ResponseObject result = (ResponseObject) parse(response.body().string(), ResponseObject.class);

        assertThat(result.getStatus(), is(200));
    }

    @Test(timeout = TIMEOUT)
    public void testGet_Failure() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/bad_request");
        final Response response = NetworkUtil.get(url.toString());
        final ResponseObject result = (ResponseObject) parse(response.body().string(), ResponseObject.class);

        assertThat(result.getStatus(), is(400));
    }

    @Test(timeout = TIMEOUT)
    public void testGet_NotFound() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/nothing");
        final Response response = NetworkUtil.get(url.toString());

        assertThat(response.code(), is(404));
    }

    @Test(timeout = TIMEOUT)
    public void testPost_Successful() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/post");
        final Response response = NetworkUtil.post(url.toString(), "{\"key:\"\"value\"}");
        final ResponseObject result = (ResponseObject) parse(response.body().string(), ResponseObject.class);

        assertThat(result.getStatus(), is(200));
    }

    @Test(timeout = TIMEOUT)
    public void testPost_Failure() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/bad_request");
        final Response response = NetworkUtil.post(url.toString(), "{\"key:\"\"value\"}");
        final ResponseObject result = (ResponseObject) parse(response.body().string(), ResponseObject.class);

        assertThat(result.getStatus(), is(400));
    }

    @Test(timeout = TIMEOUT)
    public void testPost_NotFound() throws Exception {
        final HttpUrl url = NetworkMockUtil.call("/nothing");
        final Response response = NetworkUtil.post(url.toString(), "{\"key:\"\"value\"}");

        assertThat(response.code(), is(404));
    }
}
