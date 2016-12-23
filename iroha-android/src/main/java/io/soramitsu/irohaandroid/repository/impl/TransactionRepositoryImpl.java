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

package io.soramitsu.irohaandroid.repository.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.net.IrohaHttpClient;
import io.soramitsu.irohaandroid.net.Routes;
import io.soramitsu.irohaandroid.repository.TransactionRepository;
import okhttp3.Request;
import okhttp3.Response;

import static io.soramitsu.irohaandroid.net.IrohaHttpClient.createRequest;

public class TransactionRepositoryImpl implements TransactionRepository {
    public static final String TAG = TransactionRepositoryImpl.class.getSimpleName();

    private IrohaHttpClient httpClient = IrohaHttpClient.getInstance();
    private Gson gson = new Gson();

    @Override
    public TransactionListEntity findHistory(String uuid, int limit, int offset)
            throws IOException, HttpBadRequestException {

        return findHistory(
                createRequest(
                        Routes.TRANSACTION_HISTORY_WITH_UUID
                                + "?uuid=" + uuid
                                + "&limit=" + limit
                                + "&offset=" + offset
                )
        );
    }

    @Override
    public TransactionListEntity findHistory(String uuid, String domain, String asset, int limit, int offset)
            throws IOException, HttpBadRequestException {

        return findHistory(
                createRequest(
                        Routes.TRANSACTION_HISTORY(domain, asset)
                                + "?uuid=" + uuid
                                + "&limit=" + limit
                                + "&offset=" + offset
                )
        );
    }

    private TransactionListEntity findHistory(Request request)
            throws IOException, HttpBadRequestException {
        Response response = httpClient.call(request);

        final int code = response.code();
        final String responseBody = response.body().string();
        Log.d(TAG, "find history: json[\n" + responseBody + "]\nresponse code: " + code);
        switch (code) {
            case 200:
                return gson.fromJson(responseBody, new TypeToken<TransactionListEntity>(){}.getType());
            default:
                throw new HttpBadRequestException();
        }
    }
}
