/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import click.kobaken.rxirohaandroid.Iroha;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class IrohaHttpClient {
    private static IrohaHttpClient irohaClient  = new IrohaHttpClient();

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;

    private IrohaHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Accept", "application/json");
                headerMap.put("Content-type", "application/json");

                //header設定
                Request request = original.newBuilder()
                        .headers(Headers.of(headerMap))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        //ログ出力設定
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        httpClient.addInterceptor(logging);

        client = httpClient.build();
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
