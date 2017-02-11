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

package click.kobaken.rxirohaandroid.net;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IrohaHttpClientTest {

    private final Dispatcher dispatcher = new Dispatcher() {
        @Override
        public MockResponse dispatch(final RecordedRequest request) throws InterruptedException {
            if (request == null || request.getPath() == null) {
                return mockResponse.setResponseCode(400);
            }

            if (request.getPath().equals("/error")) {
                return mockResponse.setResponseCode(500);
            }

            assertThat(request.getHeader("Accept"), is("application/json"));
            assertThat(request.getHeader("Content-type"), is("application/json"));

            switch (request.getMethod()) {
                case "GET":
                    return mockResponse.setBody("This is GET");
                case "POST":
                    return mockResponse.setBody("This is POST");
                case "PUT":
                    return mockResponse.setBody("This is PUT");
                case "DELETE":
                    return mockResponse.setBody("This is DELETE");
                default:
                    return new MockResponse().setResponseCode(404);
            }
        }
    };
    private MockWebServer mockWebServer;
    private MockResponse mockResponse;

    private IrohaHttpClient irohaHttpClient;

    @Before
    public void setUp() throws Exception {
        initMockServer();
        irohaHttpClient = IrohaHttpClient.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void get() throws Exception {
        OkHttpClient client = irohaHttpClient.get();

        final Request request = new Request.Builder().get().url(mockWebServer.url("/get")).build();
        final Response response = client.newCall(request).execute();

        assertThat(response.headers().toString(), is("Content-type: application/json\nAccept: application/json\nContent-Length: 11\n"));
        assertThat(response.body().string(), is("This is GET"));
    }

    @Test
    public void post() throws Exception {
        OkHttpClient client = irohaHttpClient.get();

        RequestBody body = RequestBody.create(null, "PUT");
        Request request = new Request.Builder().post(body).url(mockWebServer.url("/post")).build();
        Response response = client.newCall(request).execute();

        assertThat(response.headers().toString(), is("Content-type: application/json\nAccept: application/json\nContent-Length: 12\n"));
        assertThat(response.body().string(), is("This is POST"));
    }

    @Test
    public void put() throws Exception {
        OkHttpClient client = irohaHttpClient.get();

        RequestBody body = RequestBody.create(null, "PUT");
        Request request = new Request.Builder().put(body).url(mockWebServer.url("/put")).build();
        Response response = client.newCall(request).execute();

        assertThat(response.headers().toString(), is("Content-type: application/json\nAccept: application/json\nContent-Length: 11\n"));
        assertThat(response.body().string(), is("This is PUT"));
    }

    @Test
    public void delete() throws Exception {
        OkHttpClient client = irohaHttpClient.get();

        Request request = new Request.Builder().delete().url(mockWebServer.url("/delete")).build();
        Response response = client.newCall(request).execute();

        assertThat(response.headers().toString(), is("Content-type: application/json\nAccept: application/json\nContent-Length: 14\n"));
        assertThat(response.body().string(), is("This is DELETE"));
    }

    private void initMockServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(11262);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-type", "application/json");
        mockResponse = new MockResponse().setHeaders(Headers.of(headerMap)).setResponseCode(200);
    }
}