package io.soramitsu.iroha.utils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Network util.
 */
public class NetworkUtil {
    /** Endpoint url */
    // FIXME 見られて良いもの？環境変数等から取り出すか要相談
    public static String ENDPOINT_URL = "http://mock.mizuki.io/";

    /** Successful status code */
    public static final int STATUS_OK = 200;

    /** Client error status code */
    public static final int STATUS_BAD = 400;

    /** Client not found status code */
    public static final int STATUS_NOT_FOUND = 404;

    /** Media type of JSON */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private NetworkUtil() {
    }

    /**
     * Get a url.
     *
     * @param url url
     * @return Response
     * @throws IOException
     */
    public static Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        return new OkHttpClient().newCall(request).execute();
    }

    /**
     * Post to a server.
     *
     * @param url  url
     * @param json Request Format Body JSON
     * @return Response
     * @throws IOException
     */
    public static Response post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return new OkHttpClient().newCall(request).execute();
    }
}
