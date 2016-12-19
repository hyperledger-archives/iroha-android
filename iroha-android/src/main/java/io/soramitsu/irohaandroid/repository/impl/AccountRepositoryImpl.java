package io.soramitsu.irohaandroid.repository.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import io.soramitsu.irohaandroid.entity.AccountEntity;
import io.soramitsu.irohaandroid.exception.AccountDuplicateException;
import io.soramitsu.irohaandroid.exception.HttpBadRequestException;
import io.soramitsu.irohaandroid.exception.UserNotFoundException;
import io.soramitsu.irohaandroid.net.IrohaHttpClient;
import io.soramitsu.irohaandroid.net.Routes;
import io.soramitsu.irohaandroid.net.dataset.reqest.AccountRegisterRequest;
import io.soramitsu.irohaandroid.repository.AccountRepository;
import okhttp3.Request;
import okhttp3.Response;

import static io.soramitsu.irohaandroid.net.IrohaHttpClient.createRequest;

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
        Request request = IrohaHttpClient.createRequest(Routes.ACCOUNT_INFO + "?uuid=" + uuid);
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
