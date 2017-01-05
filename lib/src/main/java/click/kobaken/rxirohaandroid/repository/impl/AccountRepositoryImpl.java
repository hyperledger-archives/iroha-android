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

package click.kobaken.rxirohaandroid.repository.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import click.kobaken.rxirohaandroid.entity.AccountEntity;
import click.kobaken.rxirohaandroid.exception.AccountDuplicateException;
import click.kobaken.rxirohaandroid.exception.HttpBadRequestException;
import click.kobaken.rxirohaandroid.exception.UserNotFoundException;
import click.kobaken.rxirohaandroid.net.IrohaHttpClient;
import click.kobaken.rxirohaandroid.net.Routes;
import click.kobaken.rxirohaandroid.net.dataset.reqest.AccountRegisterRequest;
import click.kobaken.rxirohaandroid.repository.AccountRepository;
import okhttp3.Request;
import okhttp3.Response;

import static click.kobaken.rxirohaandroid.net.IrohaHttpClient.createRequest;

public class AccountRepositoryImpl implements AccountRepository {
    public static final String TAG = AccountRepositoryImpl.class.getSimpleName();

    private IrohaHttpClient httpClient = IrohaHttpClient.getInstance();
    private Gson gson = new Gson();

    @Override
    public AccountEntity register(AccountRegisterRequest body)
            throws IOException, AccountDuplicateException, HttpBadRequestException {
        String json = gson.toJson(body, AccountRegisterRequest.class);
        Request request = createRequest(Routes.ACCOUNT_REGISTER, json);
        Response response = httpClient.call(request);

        final int code = response.code();
        final String responseBody = response.body().string();
        Log.d(TAG, "register account: json[\n" + responseBody + "]\nresponse code: " + code);
        switch (code) {
            case 200:
            case 201:
                return gson.fromJson(responseBody, new TypeToken<AccountEntity>(){}.getType());
            case 400:
                throw new AccountDuplicateException();
            default:
                throw new HttpBadRequestException();
        }
    }

    @Override
    public AccountEntity findByUuid(String uuid)
            throws IOException, UserNotFoundException, HttpBadRequestException {
        Request request = createRequest(Routes.ACCOUNT_INFO + "?uuid=" + uuid);
        Response response = httpClient.call(request);

        final int code = response.code();
        final String responseBody = response.body().string();
        Log.d(TAG, "find account: json[\n" + responseBody + "]\nresponse code: " + code);
        switch (code) {
            case 200:
                return gson.fromJson(responseBody, new TypeToken<AccountEntity>(){}.getType());
            case 400:
                throw new UserNotFoundException();
            default:
                throw new HttpBadRequestException();
        }
    }
}
