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

package io.soramitsu.iroha.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.soramitsu.iroha.api.reqest.AccountRegisterRequest;
import io.soramitsu.iroha.api.reqest.AssetOperationRequest;
import io.soramitsu.iroha.entity.AccountEntity;
import io.soramitsu.iroha.entity.TransactionListEntity;
import io.soramitsu.iroha.model.OperationParameter;
import io.soramitsu.iroha.util.DateUtil;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class IrohaClient {
    private static final IrohaClient client = new IrohaClient();

    private final IrohaService irohaService;

    private IrohaClient() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        irohaService = new Retrofit.Builder()
                .baseUrl("https://point-demo.iroha.tech/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(IrohaService.class);
    }

    public static IrohaClient getInstance() {
        return client;
    }

    public Single<AccountEntity> createAccount(String publicKey, String alias) {
        final AccountRegisterRequest body = new AccountRegisterRequest();
        body.publicKey = publicKey;
        body.alias = alias;
        body.timestamp = DateUtil.currentUnixTimestamp();
        return irohaService.craeteAccount(body);
    }

    public Single<AccountEntity> fetchAccountInfo(String uuid) {
        return irohaService.fetchAccountInfo(uuid);
    }

    public Completable operation(String uuid,
                                     String command,
                                     String sender,
                                     String receiver,
                                     String value,
                                     String signature,
                                     long timestamp) {
        final AssetOperationRequest body = new AssetOperationRequest();
        body.uuid = uuid;
        body.params = new OperationParameter();
        body.params.command = command;
        body.params.value = value;
        body.params.sender = sender;
        body.params.receiver = receiver;
        body.signature = signature;
        body.timestamp = timestamp;
        return irohaService.operation(body);
    }

    public Single<TransactionListEntity> fetchTx(String uuid, int limit, int offset) {
        return irohaService.fetchTx(uuid, limit, offset);
    }

    interface IrohaService {
        @POST("account/register")
        Single<AccountEntity> craeteAccount(@Body AccountRegisterRequest payload);

        @GET("account")
        Single<AccountEntity> fetchAccountInfo(@Query("uuid") String uuid);

        @POST("asset/operation")
        Completable operation(@Body AssetOperationRequest payload);

        @GET("history/transaction")
        Single<TransactionListEntity> fetchTx(@Query("uuid") String uuid,
                                              @Query("limit") int limit,
                                              @Query("offset") int offset);
    }
}
