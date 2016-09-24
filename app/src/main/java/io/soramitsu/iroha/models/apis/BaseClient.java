package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Base client object.
 */
public class BaseClient {
    /** Endpoint url */
    public static final String ENDPOINT_URL = "http://io.mizuki.sonoko/";

    /** Successful status code */
    public static final int STATUS_OK = 200;

    /** Client error status code */
    public static final int STATUS_BAD = 400;

    /** Media type of JSON */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient okHttpClient;
    protected Gson gson;

    public BaseClient(OkHttpClient okHttpClient, Gson gson) {
        this.okHttpClient = okHttpClient;
        this.gson = gson;
    }

    /**
     * Get a url.
     *
     * @param url url
     * @return Response
     * @throws IOException
     */
    protected String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Post to a server.
     *
     * @param url  url
     * @param json Request Format Body JSON
     * @return Response
     * @throws IOException
     */
    protected String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }
}
