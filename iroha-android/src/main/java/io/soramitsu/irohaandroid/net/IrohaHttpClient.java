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

package io.soramitsu.irohaandroid.net;

import java.io.IOException;

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
//        return new Request.Builder().url(Iroha.getInstance().baseUrl + path);
        throw new RuntimeException();
    }
}
