package io.soramitsu.irohaandroid.net;

import java.io.IOException;

import io.soramitsu.irohaandroid.Iroha;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IrohaHttpClient {
    private static final IrohaHttpClient irohaClient  = new IrohaHttpClient();

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;

    private IrohaHttpClient() {
        client = new OkHttpClient.Builder().build();
    }

    public static IrohaHttpClient getInstance() {
        return irohaClient;
    }

    public Response call(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    public static Request createRequest(String path) {
        return request(path).build();
    }

    public static Request createRequest(String path, String paramsJsonString) {
        RequestBody body = RequestBody.create(JSON, paramsJsonString);
        return request(path)
                .post(body)
                .build();
    }

    private static Request.Builder request(String path) {
        return new Request.Builder().url(Iroha.getInstance().baseUrl + path);
    }
}
